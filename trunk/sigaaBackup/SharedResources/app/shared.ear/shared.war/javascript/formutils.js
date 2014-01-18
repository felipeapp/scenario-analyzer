
Remocao = Class.create();

Remocao.prototype = {

	initialize : function(formId) {
		this.form = getEl(formId).dom;

		this.setFormAction('remove');
		this.changeElements();
		this.addConfirmElement();
	},

	// Muda a action do form para a action de remo??o
	setFormAction : function(newAction) {
		var action = this.form.action;
		var index = action.indexOf('dispatch=');
		if (index != -1) {
			this.form.action = action.substring(0, index + 9) + 'remove&voltando=true';
		} else {
			this.form.action = action + '?dispatch=remove&voltando=true';
		}
	},

	// Adiciona ao form um elemento hidden contendo o atributo confirm = true
	addConfirmElement : function () {
		var element = document.createElement('input');
		element.setAttribute('type', 'hidden');
		element.setAttribute('name', 'confirm');
		element.setAttribute('value', 'true');
		this.form.appendChild(element);
	},

	// Muda o estilo dos elementos do form
	changeElements : function () {
		var elements = $A(Form.getElements(this.form));
		elements.each(
			function(e) {
				if (e.type != 'button' && e.type != 'submit') {
					e.setAttribute('readOnly', 'readonly');
					e.className = 'removeField';
				}
				if (e.type == 'select-one' || e.type == 'select-multiple')
					e.disabled = true;
				if (e.type == 'radio')
					e.disabled = true;
			}
		);
		// escondendo rodape com msg de campos obrigatorios
		if (getEl('formRodape') != null) {
			getEl('formRodape').dom.style.visibility = 'hidden;'
		}
	}
}

function cancelar(formId) {
	var form = getEl(formId);
	var action = form.dom.action;
	var index = action.indexOf('dispatch=');

	if (index != -1) {
		var currentDispatch = window.location.href.substring(index + 9);
		document.location.href = action.substring(0, index + 9) + 'cancel&voltando=true&currentDispatch=' + currentDispatch;
	} else {
		document.location.href = action + '?dispatch=cancel&voltando=true';
	}

}

function setValueOnCheck(elem, elemId) {
	if (elem.checked)
		getEl(elemId).dom.value = true;
	else
		getEl(elemId).dom.value = false;
}


function toggleClass(className) {
	var elements = $A(document.getElementsByClassName(className));
	elements.each(
		function(e) {
			if (Element.visible(e))
				Effect.BlindUp(e);
			else
				Effect.BlindDown(e);
		}
	);
}
var Field = new Object();
Object.extend(Field,  {
	check : function(element) {
		var field = getEl(element).dom;
		if (field.type == 'checkbox' || field.type == 'radio') {
			field.checked = true;
		}
	}
});

function submitTablePage(img) {
	var page = img.value;
	img.form.action = img.form.action + "&page="+page;
	img.form.submit();
}