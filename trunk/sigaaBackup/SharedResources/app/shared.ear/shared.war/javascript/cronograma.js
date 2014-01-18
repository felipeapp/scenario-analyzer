Cronograma = Class.create();

Cronograma.prototype = {

	// Construtor
	initialize: function(formId, mesesString, numeroAtividadesInicial) {
		this.form = $(formId);
		this.sequenciaMeses = mesesString.split(',');
		this.numeroMeses = this.sequenciaMeses.length - 1;

		this.corpo = $A($('cronograma').getElementsByTagName('tbody')).first();
		this.sequenciaAtividade = new Number(numeroAtividadesInicial);

		// Buscar checkboxes e conectar aos eventos
		var checkboxes = Form.getInputs(this.form, 'checkbox');
		this.preparar(checkboxes);

		// Marcar as celulas cujos checkboxes estejam marcados
		checkboxes.each( function(node){
			marcarCelulaCronograma(node) ;
		});

	},

	// Adicionar uma atividade a lista
	adicionarAtividade: function() {
		var tr = Builder.node('tr');

		// Criar celula contendo a descricao da atividade
		var td = Builder.node('td');
		var atividade = Builder.node('textarea' , {
			style: 'width:95%;',
			name:  'telaCronograma.atividade',
			tabIndex: this.sequenciaAtividade + 1,
			rows: 2

		});
		td.appendChild(atividade);
		tr.appendChild(td);

		// Criar celulas contendo os checkboxes
		var checkboxes = new Array(this.numeroMeses);
		for (var i = 0; i < this.numeroMeses; i++) {
			td = Builder.node('td', {align: 'center'});
			var checkbox = Builder.node('input', {
				type: 'checkbox',
				name: 'telaCronograma.calendario',
				value: this.sequenciaAtividade + '_' + this.sequenciaMeses[i]

			});
			Element.addClassName(checkbox, 'noborder');
			td.appendChild(checkbox);
			tr.appendChild(td);

			checkboxes[i] = checkbox;
		}

		// Criar celula com o link para remocao
		td = Builder.node('td', {align: 'center'});
		var remover =  Builder.node('a', {
			href : '#',
			onclick : 'javascript:cronograma.removerAtividade(this);'
		});
		var icone = Builder.node('img', {
			src : 	'/sigaa/img/cronograma/remover.gif',
			alt:   	'Remover Atividade',
			title: 	'Remover Atividade'
		});
		remover.appendChild(icone);
		td.appendChild(remover);
		tr.appendChild(td);

		this.corpo.appendChild(tr);
		this.preparar(checkboxes);
		this.sequenciaAtividade++;
	},

	// Remover uma atividade do cronograma
	removerAtividade: function(link) {
		this.corpo.removeChild(link.parentNode.parentNode);
	},

	// Desmarcar todos os meses do cronograma
	limpar: function() {
		var checkboxes = Form.getInputs(this.form, 'checkbox');
		checkboxes.each( function(node) {
			node.checked = false;
			marcarCelulaCronograma(node);
		});
	},

	// Marcar a celula que contem o checkbox
	selecionarMes: function(event) {
		marcarCelulaCronograma(Event.element(event));
	},

	// Associar evento de selecao aos checkboxes
	preparar: function(checkboxes) {
		var crono = this;
		checkboxes.each( function(node) {
			Event.observe(node, 'click', crono.selecionarMes.bindAsEventListener(node));
		});
	}
}

function marcarCelulaCronograma(checkbox) {
	if (checkbox.checked) {
		Element.addClassName(checkbox.parentNode, 'selecionado');
	} else {
		Element.removeClassName(checkbox.parentNode, 'selecionado');
	}
}
