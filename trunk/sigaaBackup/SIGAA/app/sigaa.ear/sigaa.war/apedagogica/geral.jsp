<ul>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_PAP} %>">
	<li>Grupo de Atividades
		<ul>
			<li> <h:commandLink action="#{grupoAtividadesAP.preCadastrar}" value="Cadastrar" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{grupoAtividadesAP.listar}" value="Listar/Alterar" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	<li>Gerenciar Inscrições 
		<ul>
			<li>  <h:commandLink action="#{inscricaoAtividadeAP.preCadastrarGestor}" value="Inscrever Participante em Atividade"/>
			<li>  <h:commandLink action="#{inscricaoAtividadeAP.preExcluirGestor}" value="Remover Participação de Atividade"/>
			<li> <h:commandLink action="#{alteraSituacaoInscricaoAP.consultar}" value="Alterar Situacão da Inscrição" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{certificadoParticipacaoAP.consultar}" value="Emitir Certificado de Participação" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{geracaoListaPresencaAP.consultar}" value="Gerar Lista de Presença" onclick="setAba('geral')" /> </li>
			<li> <h:commandLink action="#{notificarParticipanteAP.consultar}" value="Notificar Participantes em Atividade" onclick="setAba('geral')" /> </li>
		</ul>
	</li>
	
	<li>Relatórios 
		<ul>
			<li>  <h:commandLink action="#{relatorioAtividadeAP.consultar}" value="Relatório de Atividades"/>
			<li>  <h:commandLink action="#{relatorioAtividadeAP.consultarQuantitativo}" value="Relatório Quantitativo de Atividades"/>
		</ul>
	</li>
	</ufrn:checkRole>
</ul>