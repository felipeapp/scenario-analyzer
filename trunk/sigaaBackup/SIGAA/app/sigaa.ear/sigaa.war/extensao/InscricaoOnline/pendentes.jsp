<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Quantitativo de Inscritos em Cursos e Eventos de Extens�o pela �rea P�blica</h2>
    
    
    <div class="descricaoOperacao">
    	Lista de inscritos por ordem de Data de Cadastro.<br/>
    	Significados das situa��es dos inscritos na A��o de Extens�o:
    	<br/>
    	<br/>    	
    	<b>Aceitas:</b> Indica que a inscri��o foi aceita pela coordena��o da a��o de extens�o e assegura a participa��o da pessoa no curso/evento.<br/>
    	<b>Confirmadas:</b> Indica que o participante confirmou a participa��o respondendo ao e-mail que recebeu do sistema. Sua inscri��o poder� ser aceita ou recusada pela coordena��o do curso/evento.<br/>
    	<b>Pendentes:</b> Indica que o participante ainda n�o respondeu ao e-mail que recebeu do sistema.<br/>
    	<b>Recusadas:</b> Indica que a coordena��o do curso/evento negou a inscri��o da pessoa.<br/>
    	<b>Canceladas:</b> Indica que o participante se inscreveu, mas desistiu do evento e cancelou a inscri��o atrav�s do portal p�blico.<br/>
    </div>
    
	
		<table class="formulario" width="80%">
		  <caption>Dados da a��o de Extens�o</caption>
			<tr>			
				<th><b>C�digo:</b></th>
				<td><h:outputText value="#{inscricaoAtividade.obj.atividade.codigo}" id="codigo" /></td>
			</tr>
			<tr>
				<th><b>T�tulo:</b></th>
				<td><h:outputText value="#{inscricaoAtividade.obj.atividade.titulo}" id="titulo" /></td>
			</tr>
			<tr>
				<th><b>Coordena��o:</b></th>
				<td><h:outputText value="#{inscricaoAtividade.obj.atividade.coordenacao.servidor.pessoa.nome}" id="coord" /></td>
			</tr>
			<tr>
				<th><b>Per�odo:</b></th>
				<td>
					<h:outputText value="#{inscricaoAtividade.obj.atividade.dataInicio}" id="inicio"> 
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
					at� 
					<h:outputText value="#{inscricaoAtividade.obj.atividade.dataFim}" id="fim">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
				</td>
			</tr>
		</table>
<br/>


    <div class="infoAltRem">
        <h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;" id="labViewInscricao"/> Visualizar Inscri��o
        <h:graphicImage value="/img/email_go.png" style="overflow: visible;" id="labReenviarInscricao"/>: Reenviar Senha de Acesso
    </div>

	<c:set value="#{inscricaoAtividade.inscricoesNaoConfirmadas}" var="naoConfirmados" />
	
<h:form id="form">
	<table class="listagem" width="100%">
			<caption>Lista das Inscri��es nessa A��o</caption>
			<thead>
				<tr>
					<th style="text-align:center" width="8%">
						<a id="marcar" href="javascript:void(0)" onclick="marcarTodosCheckboxes();">TODOS</a>
					</th>
					<th width="10%" style="text-align: center;">Data do Cadastro</th>
					<th width="30%">Nome Completo</th>
					<th width="20%">E-mail</th>
					<th width="20%">Institui��o</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				
				<tr>
					<td colspan="9" class="subFormulario">Inscri��es PENDENTES (${fn:length(naoConfirmados)})</td>
				</tr>
				<c:if test="${empty naoConfirmados}">
					<tr>
						<td colspan="9">N�o existem inscri��es <b>pendentes de confirma��o</b> nesta a��o</td>
					</tr>
				</c:if>
						<c:forEach items="#{naoConfirmados}" 
								var="participante" varStatus="count">
							<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td style="text-align: center;">
									<h:selectBooleanCheckbox value="#{participante.marcado}" />
								</td>
								<td style="text-align:center">
									<h:outputText value="#{participante.dataNascimento}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText> 
								</td>
								<td>${participante.nome}</td>
								<td>${participante.email}</td>
								<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
								
								<td width="2%">
									<h:commandLink title="Visualizar inscri��o" immediate="true" id="viewInformacoesAceito"
										action="#{inscricaoAtividade.visualizarDadosInscrito}">
										<f:param name="id" value="#{participante.id}" />
										<h:graphicImage url="/img/monitoria/businessman_view.png" />
									</h:commandLink>
								</td>

								<td width="2%">
		                           <h:commandLink title="Reenviar senha de acesso" immediate="true" id="reenviarSenhaAceito"
		                                   action="#{inscricaoParticipantes.reenviarSenhaParticipanteOnline}" 
		                                   rendered="#{ !participante.cancelado }"
		                                   onclick="return confirm('Confirma o Reenvio da Senha de Acesso?');" >
		                               <f:param name="id" value="#{participante.id}" />
		                               <h:graphicImage url="/img/email_go.png"/>
		                           </h:commandLink>
		                         </td>
		                         
							</tr>
						</c:forEach>
					</tbody>
					
				<tfoot>
					<tr>
						<td colspan="9" align="center">
							<h:commandButton value="Confirmar Inscri��o" action="#{inscricaoAtividade.confirmaInscricaoParticipantes}" id="enviar" />
							<h:commandButton value="Cancelar" action="#{inscricaoAtividade.voltaTelaGerenciaInscricoes}" id="cancelar" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
				
			</tbody>
		</table>
</h:form>
</f:view>

<script type="text/javascript">

var mark = false;

function marcarTodosCheckboxes(){
	var checkboxes = document.getElementsByTagName('INPUT');
	mark = mark ? false : true;
	$('marcar').innerHTML = mark ? 'NENHUM' : 'TODOS';
	for (i in checkboxes) {
		var input = checkboxes[i];
		if(input.type == 'checkbox')
			input.checked = mark;
	}
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>