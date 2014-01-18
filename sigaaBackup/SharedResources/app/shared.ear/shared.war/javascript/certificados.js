
var striped = false;

/*
 * Constrói tabela de Qualificações do curso de um aluno
 */
function retrieveInfoQualificacoes() {
	createLoadingBar();
	Element.show('loading');
	Element.hide('qualificacoes');
	var idDiscente = $F('idDiscente');

	var parseQualificacaoXML = function(t) {
		listaQualificacoes = new Array();
		if (t.responseXML.getElementsByTagName("response")[0] == null) {
			$('qualificacoes').innerHTML = "<i>Esse discente não tem direito a nenhum certificado ainda.</i>";
			Effect.BlindUp('loading', { duration : 0.5 });
			Effect.BlindDown('qualificacoes', { duration : 0.5 });
			return;
		}
		items = $A(t.responseXML.getElementsByTagName("response")[0].childNodes);

		items.each(
			function(item) {
				name = item.getElementsByTagName('name')[0].firstChild.data;
				value = item.getElementsByTagName('value')[0].firstChild.data;
				qualificacao = {
					'id' : value,
					'descricao' : name
				};
				listaQualificacoes.push(qualificacao);
			}
		);

		if (listaQualificacoes.length > 0)
			buildTable($A(listaQualificacoes), 'Qualificações Possíveis Para esse Discente', 'qualificacao');
		else
			Effect.BlindUp('loading', { duration : 0.5 });
	}

	var ajax = new Ajax.Request('/sigaa/ajaxQualificacoesDiscente',
		{
			method: 'get',
			parameters: 'idDiscente=' + idDiscente,
			onSuccess: parseQualificacaoXML
		});

}

/*
 * Constrói tabela de qualificacoes
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
			var hiddenId = '<input type="hidden" name="id" value="'+item.id+'"/>';
			var button = '<input type="button" onclick="submitMethod(\'exibirCertificado\', this, \'qualif_'+item.id+'\')" value="Gerar Certificado >>"/>'

			td = buildLine('td', item.descricao, item.id, hiddenId + button);
			tbody.appendChild(td);
		}
	);

	// Anexa elementos à tabela
	table.appendChild(caption);
	table.appendChild(thead);
	table.appendChild(tbody);

	// Anexa tabela ao div
	var div = $('qualificacoes');
	while (div.childNodes[0])
		div.removeChild(div.childNodes[0]);
	div.appendChild(table);

	Effect.BlindUp('loading', { duration : 0.5 });
	Effect.BlindDown('qualificacoes', { duration : 0.5 });
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

	var td2 = document.createElement(columnType);
	td2.setAttribute('align', 'right');
	td2.setAttribute('width', '10%');

	var form = '<form method="post" action="/sigaa/ensino/gerarCertificado.do">'
				+ '<input type="hidden" name="dispatch" id="qualif_'+value2+'"/>'
				+ value3
				+ '</form>';
	td2.innerHTML = form;

	tr.appendChild(td1);
	tr.appendChild(td2);

	return tr;
}

function createLoadingBar() {
	div = $('loading');

	while (div.childNodes[0])
		div.removeChild(div.childNodes[0]);

	p = document.createElement('p');
	p.setAttribute('align', 'center');
	text = document.createTextNode('Carregando lista de qualificações...');
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

