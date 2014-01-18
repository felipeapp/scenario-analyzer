
function readMsg(id, folder, page) {
	var url = '/sigaa/mensagem/verMensagem.do?mensagem.id=' + id + '&folder=' + folder + '&page=' + page;
	painelMensagemLeitura.show(url, id);
}

function readMsgPatrimonial(id, folder, page, chamado) {
	var url = '/sipac/verMensagem.do?mensagem.id=' + id + '&folder=' + folder + '&page=' + page + '&chamado=' + chamado;
	painelMensagemLeitura.show(url, id);
}

function showFolder(folder, page) {
	var url = '/sigaa/mensagem/showFolder.do?folder=' + folder + '&page=1';
	document.location.href = url;
}

function confirmarRemocao() {
    ids = $('formRemocao')['mensagem.idMensagem'];
    if (ids == null || ids == undefined || ids.length == 0)
      return 0;

    for (i = 0; i < ids.length; i++) {
      if (ids[i].checked) {
        if (confirm("Deseja realmente remover as mensagens selecionadas?")) {
            $('formRemocao').submit();
            return true;
         } else
           return false;
      }
    }

    if(ids != null){
		if (ids.checked) {
		    if (confirm("Deseja realmente remover as mensagens selecionadas?")) {
		        $('formRemocao').submit();
		        return true;
		     } else
		       return false;
		  }
	}

    alert("Seleção vazia. Marque as mensagens para remoção");
    return false;
}

function doAction(confirmar, formName) {
	var form = $(formName);
	var confirmado = true;
	if (confirmar) {
		confirmado = confirm('Deseja realmente apagar esta mensagem?');
		if (confirmado) {
			form.submit();
		}
	} else {
		var url = form.action + '?' +  Form.serialize(form);
		mensagem.show(1, url);
		painelMensagemLeitura.hide();
	}

}

function setUsuario(idUsuario, nome) {
  window.location = "/sigaa/mensagem/enviaMensagem.do?idUsuario=" + idUsuario + "&acao=5";
}

function encaminhar(idUsuario, idMensagem) {
  window.location = "/sigaa/mensagem/enviaMensagem.do?idUsuario=" + idUsuario + "&idMensagemEncaminhar=" + idMensagem + "&acao=5";
}
