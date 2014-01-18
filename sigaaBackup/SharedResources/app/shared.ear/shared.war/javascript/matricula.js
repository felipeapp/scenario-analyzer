
var striped = false;

function retrieveInfoAluno() {
	name = $F('modelDiscente');
	value = $F('makeDiscente');
	alunos = new Array();
	aluno = {
		'id' : value,
		'descricao' : name
	}
	alunos.push(aluno);
	buildTable($A(alunos), 'Matricular Aluno', 'aluno');
}

/*
 * Constrói tabela de Turmas de entrada no primeiro passo
 * da matrícula de alunos (técnico)
 */
function retrieveInfoTurmas() {
	createLoadingBar();
	Element.show('loading');
	Element.hide('turmas');
	var idDiscente = $F('matricula:idDiscente');

	var parseTurmaXML = function(t) {
		listaTurmas = new Array();
		items = $A(t.responseXML.getElementsByTagName("response")[0].childNodes);

		items.each(
			function(item) {
				name = item.getElementsByTagName('name')[0].firstChild.data;
				value = item.getElementsByTagName('value')[0].firstChild.data;
				turma = {
					'id' : value,
					'descricao' : name
				};
				listaTurmas.push(turma);
			}
		);

		if (listaTurmas.length > 0)
			buildTable($A(listaTurmas), 'Turmas Matriculadas', 'turma');
		else
			Effect.BlindUp('loading', { duration : 0.5 });		
	}

	var ajax = new Ajax.Request('/sigaa/ajaxTurmasDiscente',
		{
			method: 'get',
			parameters: 'idDiscente=' + idDiscente,
			onSuccess: parseTurmaXML
		});

	//retrieveURL('/sigaa/ajaxTurmaEntradaTecnico?cursoId='+idCurso, parseTurmaXML);

}

/*
 * Constrói tabela de turmas
 */
function buildTable(items, title, type) {
	// Cria tabela
	var table = document.createElement('table');
	table.className = 'listagem';

	// Cria elementos da tabela
	var caption = document.createElement('caption');
	caption.innerHTML = title;

	var thead = document.createElement('thead');
	thead.appendChild(buildLine('th', 'Descrição', '', ''));

	var tbody = document.createElement('tbody');
	items.each(
		function (item) {
			var hiddenType = '<input type="hidden" name="type" value="'+type+'"/>';
			var hiddenId = '<input type="hidden" name="id" value="'+item.id+'"/>';
			var button = '<input type="button" onclick="submitMethod(\'selecionaDisciplina\', this, \'d_'+item.id+'\')" value="Matricular >>"/>'

			td = buildLine('td', item.descricao, item.id, hiddenType + hiddenId + button);
			tbody.appendChild(td);
		}
	);

	// Anexa elementos à tabela
	table.appendChild(caption);
	table.appendChild(thead);
	table.appendChild(tbody);

	// Anexa tabela ao div
	var div = $('turmas');
	while (div.childNodes[0])
		div.removeChild(div.childNodes[0]);
	div.appendChild(table);

	Effect.BlindUp('loading', { duration : 0.5 });
	Effect.BlindDown('turmas', { duration : 0.5 });
}

/*
 * Constrói uma linha de uma tabela
 */
function buildLine(columnType, value1, value2, value3) {
	var tr = document.createElement('tr');
	if (columnType != 'th')
		tr.className = striped ? 'linhaPar' : 'linhaImpar';
	striped = !striped;

	var td1 = document.createElement(columnType);
	td1.innerHTML = value1;
	//var td2 = document.createElement(columnType);
	//td2.setAttribute('align', 'right');
	//td2.setAttribute('width', '10%');

	//var form = '<form method="post" action="/sigaa/ensino/matriculaDisciplina.do">'
	//			+ '<input type="hidden" name="dispatch" id="d_'+value2+'"/>'
	//			+ value3
	//			+ '</form>';
	//td2.innerHTML = form;

	tr.appendChild(td1);
	//tr.appendChild(td2);

	return tr;
}

function createLoadingBar() {
	div = $('loading');

	while (div.childNodes[0])
		div.removeChild(div.childNodes[0]);

	p = document.createElement('p');
	p.setAttribute('align', 'center');
	text = document.createTextNode('Carregando lista de turmas...');
	br1 = document.createElement('br');
	img = document.createElement('img');
	img.src = '/sigaa/img/indicator_bar.gif';
	br2 = document.createElement('br');
	space = document.createTextNode(' ');

	p.appendChild(text);
	p.appendChild(br1);
	p.appendChild(img);
	p.appendChild(br2);
	p.appendChild(space);

	div.appendChild(p);
}

