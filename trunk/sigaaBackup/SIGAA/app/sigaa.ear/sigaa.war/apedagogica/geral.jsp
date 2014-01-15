<ul>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_PAP} %>">
	<li>Grupo de Atividades
		<ul>
			<li> <h:commandLink action="#{grupoAtividadesAP.preCadastrar}" value="Cadastrar" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{grupoAtividadesAP.listar}" value="Listar/Alterar" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	<li>Gerenciar Inscri��es 
		<ul>
			<li>  <h:commandLink action="#{inscricaoAtividadeAP.preCadastrarGestor}" value="Inscrever Participante em Atividade"/>
			<li>  <h:commandLink action="#{inscricaoAtividadeAP.preExcluirGestor}" value="Remover Participa��o de Atividade"/>
			<li> <h:commandLink action="#{alteraSituacaoInscricaoAP.consultar}" value="Alterar Situac�o da Inscri��o" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{certificadoParticipacaoAP.consultar}" value="Emitir Certificado de Participa��o" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{geracaoListaPresencaAP.consultar}" value="Gerar Lista de Presen�a" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{notificarParticipanteAP.consultar}" value="Notificar Participantes em Atividade" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	
	<li>Relat�rios 
		<ul>
			<li>  <h:commandLink action="#{relatorioAtividadeAP.consultar}" value="Relat�rio de Atividades"/>
			<li>  <h:commandLink action="#{relatorioAtividadeAP.consultarQuantitativo}" value="Relat�rio Quantitativo de Atividades"/>
		</ul>
	</li>
	</ufrn:checkRole>
</ul>