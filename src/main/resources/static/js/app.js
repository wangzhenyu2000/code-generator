// --- Common ---
function downloadZip(blob, filename) {
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(url);
}

// --- Scaffold ---
(function() {
    const groupIdEl = document.getElementById('groupId');
    const artifactIdEl = document.getElementById('artifactId');
    const packageNameEl = document.getElementById('packageName');
    if (!groupIdEl) return;

    function autoFillPackage() {
        const gid = groupIdEl.value.trim();
        const aid = artifactIdEl.value.trim().replace(/-/g, '.');
        if (gid && aid) {
            packageNameEl.value = gid + '.' + aid;
        }
    }
    groupIdEl.addEventListener('input', autoFillPackage);
    artifactIdEl.addEventListener('input', autoFillPackage);

    document.getElementById('scaffoldForm').addEventListener('submit', async (e) => {
        e.preventDefault();

        const gid = groupIdEl.value.trim();
        const aid = artifactIdEl.value.trim();
        if (!gid) { showAlert('请填写 Group ID', 'warning'); return; }
        if (!aid) { showAlert('请填写 Artifact ID', 'warning'); return; }

        const btn = document.getElementById('generateBtn');
        const btnText = document.getElementById('btnText');
        const btnSpinner = document.getElementById('btnSpinner');
        btn.disabled = true;
        btnText.textContent = '生成中...';
        btnSpinner.classList.remove('d-none');

        try {
            const resp = await fetch('/api/scaffold/generate', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    groupId: groupIdEl.value.trim(),
                    artifactId: artifactIdEl.value.trim(),
                    packageName: packageNameEl.value.trim() || (groupIdEl.value.trim() + '.' + artifactIdEl.value.trim().replace(/-/g, '.')),
                    projectName: document.getElementById('projectName').value.trim() || artifactIdEl.value.trim(),
                    springBootVersion: document.getElementById('springBootVersion').value,
                    javaVersion: document.getElementById('javaVersion').value,
                    port: parseInt(document.getElementById('port').value) || 8080
                })
            });
            if (!resp.ok) {
                const err = await resp.json();
                throw new Error(err.message || '生成失败');
            }
            const blob = await resp.blob();
            const filename = (artifactIdEl.value.trim() || 'project') + '-scaffold.zip';
            downloadZip(blob, filename);
            showAlert('生成成功，下载已开始', 'success');
        } catch (err) {
            showAlert(err.message, 'danger');
        } finally {
            btn.disabled = false;
            btnText.textContent = '生成并下载';
            btnSpinner.classList.add('d-none');
        }
    });
})();

// --- Reverse ---
const DB_DRIVERS = {
    mysql: 'com.mysql.cj.jdbc.Driver',
    postgresql: 'org.postgresql.Driver',
    oracle: 'oracle.jdbc.OracleDriver',
    sqlserver: 'com.microsoft.sqlserver.jdbc.SQLServerDriver'
};

const DB_URL_TEMPLATES = {
    mysql: 'jdbc:mysql://localhost:3306/dbname?useSSL=false&serverTimezone=UTC',
    postgresql: 'jdbc:postgresql://localhost:5432/dbname',
    oracle: 'jdbc:oracle:thin:@localhost:1521:ORCL',
    sqlserver: 'jdbc:sqlserver://localhost:1433;databaseName=dbname'
};

(function() {
    const dbTypeEl = document.getElementById('dbType');
    const jdbcUrlEl = document.getElementById('jdbcUrl');
    if (!dbTypeEl) return;

    dbTypeEl.addEventListener('change', () => {
        jdbcUrlEl.value = DB_URL_TEMPLATES[dbTypeEl.value] || '';
    });
    jdbcUrlEl.value = DB_URL_TEMPLATES[dbTypeEl.value];
    refreshSavedConnections();
})();

// --- 连接配置管理 ---
async function refreshSavedConnections() {
    try {
        const resp = await fetch('/api/connection-config');
        const result = await resp.json();
        if (result.code !== 200) return;
        const list = result.data || [];
        const sel = document.getElementById('savedConnSelect');
        if (!sel) return;
        sel.innerHTML = '<option value="">-- 手动填写 --</option>' +
            list.map(c => `<option value="${c.id}">${c.name} (${c.dbType})</option>`).join('');
    } catch (e) { /* server not ready, ignore */ }
}

async function loadSavedConnection() {
    const sel = document.getElementById('savedConnSelect');
    const id = sel.value;
    if (!id) { showAlert('请先选择一个已保存的连接', 'warning'); return; }
    try {
        const resp = await fetch('/api/connection-config');
        const result = await resp.json();
        const config = (result.data || []).find(c => c.id == id);
        if (!config) { showAlert('连接配置不存在', 'warning'); return; }
        document.getElementById('dbType').value = config.dbType || 'mysql';
        document.getElementById('jdbcUrl').value = config.jdbcUrl || '';
        document.getElementById('username').value = config.username || '';
        document.getElementById('password').value = config.password || '';
        document.getElementById('schemaPattern').value = config.schemaPattern || '';
        showAlert('已加载: ' + config.name, 'info');
        testConnection();
    } catch (e) {
        showAlert('加载失败: ' + e.message, 'danger');
    }
}

async function saveCurrentConnection() {
    const jdbcUrl = document.getElementById('jdbcUrl').value.trim();
    const username = document.getElementById('username').value.trim();
    if (!jdbcUrl || !username) { showAlert('请先填写 JDBC URL 和用户名', 'warning'); return; }
    const name = prompt('请输入连接名称（如：本地MySQL开发库）', '');
    if (!name) return;
    const dbType = document.getElementById('dbType').value;
    try {
        const resp = await fetch('/api/connection-config', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                name: name,
                dbType: dbType,
                driverClassName: DB_DRIVERS[dbType],
                jdbcUrl: jdbcUrl,
                username: username,
                password: document.getElementById('password').value,
                schemaPattern: document.getElementById('schemaPattern').value.trim()
            })
        });
        const result = await resp.json();
        if (result.code !== 200) throw new Error(result.message || '保存失败');
        showAlert('连接配置已保存', 'success');
        refreshSavedConnections();
    } catch (e) {
        showAlert('保存失败: ' + e.message, 'danger');
    }
}

async function deleteSavedConnection() {
    const sel = document.getElementById('savedConnSelect');
    const id = sel.value;
    if (!id) { showAlert('请先选择一个已保存的连接', 'warning'); return; }
    if (!confirm('确定删除该连接配置？')) return;
    try {
        const resp = await fetch('/api/connection-config/' + id, { method: 'DELETE' });
        const result = await resp.json();
        if (result.code !== 200) throw new Error(result.message || '删除失败');
        showAlert('已删除', 'success');
        refreshSavedConnections();
    } catch (e) {
        showAlert('删除失败: ' + e.message, 'danger');
    }
}

let tableNames = [];

async function testConnection() {
    const btn = document.getElementById('testConnBtn');
    const text = document.getElementById('testConnText');
    const spinner = document.getElementById('testConnSpinner');
    btn.disabled = true;
    text.textContent = '连接中...';
    spinner.classList.remove('d-none');

    const dbType = document.getElementById('dbType').value;
    const schema = document.getElementById('schemaPattern').value.trim() || null;

    try {
        const resp = await fetch('/api/reverse/test-connection', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                driverClassName: DB_DRIVERS[dbType],
                jdbcUrl: document.getElementById('jdbcUrl').value.trim(),
                username: document.getElementById('username').value.trim(),
                password: document.getElementById('password').value,
                schemaPattern: schema
            })
        });
        const result = await resp.json();
        if (result.code !== 200) {
            throw new Error(result.message || '连接失败');
        }
        tableNames = result.data || [];
        renderTableList(tableNames);
        document.getElementById('step2Card').classList.remove('d-none');
        document.getElementById('connSuccessMsg').textContent =
            `连接成功！发现 ${tableNames.length} 张表。`;
        showAlert('', 'success'); // clear errors
    } catch (err) {
        showAlert(err.message, 'danger');
        document.getElementById('step2Card').classList.add('d-none');
    } finally {
        btn.disabled = false;
        text.textContent = '测试连接';
        spinner.classList.add('d-none');
    }
}

function renderTableList(tables) {
    const container = document.getElementById('tableList');
    container.innerHTML = tables.map(t =>
        `<label><input type="checkbox" value="${t}" class="table-check"> ${t}</label>`
    ).join('');
}

function selectAll(checked) {
    document.querySelectorAll('.table-check').forEach(cb => cb.checked = checked);
}

async function generateReverse() {
    // 客户端校验
    const pkgName = document.getElementById('reversePackageName').value.trim();
    const jdbcUrl = document.getElementById('jdbcUrl').value.trim();
    const username = document.getElementById('username').value.trim();
    if (!jdbcUrl) {
        showAlert('请先填写 JDBC URL 并测试连接', 'warning');
        return;
    }
    if (!username) {
        showAlert('请输入数据库用户名', 'warning');
        return;
    }
    if (!pkgName) {
        showAlert('请填写 Package Name', 'warning');
        return;
    }

    const selected = Array.from(document.querySelectorAll('.table-check:checked')).map(cb => cb.value);
    if (selected.length === 0) {
        showAlert('请至少选择一张表', 'warning');
        return;
    }

    const btn = document.getElementById('genBtn');
    const text = document.getElementById('genBtnText');
    const spinner = document.getElementById('genBtnSpinner');
    btn.disabled = true;
    text.textContent = '生成中...';
    spinner.classList.remove('d-none');

    const dbType = document.getElementById('dbType').value;
    const schema = document.getElementById('schemaPattern').value.trim() || null;

    try {
        const resp = await fetch('/api/reverse/generate', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({
                driverClassName: DB_DRIVERS[dbType],
                jdbcUrl: document.getElementById('jdbcUrl').value.trim(),
                username: document.getElementById('username').value.trim(),
                password: document.getElementById('password').value,
                schemaPattern: schema,
                tableNames: selected,
                packageName: document.getElementById('reversePackageName').value.trim(),
                author: document.getElementById('author').value.trim() || 'developer',
                moduleName: document.getElementById('moduleName').value.trim() || ''
            })
        });
        if (!resp.ok) {
            const err = await resp.json();
            throw new Error(err.message || '生成失败');
        }
        const blob = await resp.blob();
        downloadZip(blob, 'crud-code.zip');
        showAlert('生成成功，下载已开始', 'success');
    } catch (err) {
        showAlert(err.message, 'danger');
    } finally {
        btn.disabled = false;
        text.textContent = '生成并下载';
        spinner.classList.add('d-none');
    }
}
