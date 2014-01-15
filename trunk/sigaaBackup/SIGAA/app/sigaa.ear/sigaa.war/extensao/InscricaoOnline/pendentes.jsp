<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Quantitativo de Inscritos em Cursos e Eventos de Extensão pela Área Pública</h2>
    
    
    <div class="descricaoOperacao">
    	Lista de inscritos por ordem de Data de Cadastro.<br/>
    	Significados das situações dos inscritos na Ação de Extensão:
    	<br/>
    	<br/>    	
    	<b>Aceitas:</b> Indica que a inscrição foi aceita pela coordenação da ação de extensão e assegura a participação da pessoa no curso/evento.<br/>
    	<b>Confirmadas:</b> Indica que o participante confirmou a participação respondendo ao e-mail que recebeu do sistema. Sua inscrição poderá ser aceita ou recusada pela coordenação do curso/evento.<br/>
    	<b>Pendentes:</b> Indica que o participante ainda não respondeu ao e-mail que recebeu do sistema.<br/>
    	<b>Recusadas:</b> Indica que a coordenação do curso/evento negou a inscrição da pessoa.<br/>
    	<b>Canceladas:</b> Indica que o participante se inscreveu, mas desistiu do evento e cancelou a inscrição através do portal público.<br/>
    </div>
    
	
		<table class="formulario" width="80%">
		  <caption>Dados da ação de Extensão</caption>
			<tr>			
				<th><b>Código:</b></th>
				<td><h:outputText value="#{inscricaoAtividade.obj.atividade.codigo}" id="codigo" /></td>
			</tr>
			<tr>
				<th><b>Título:</b></th>
				<td><h:outputText value="#{inscricaoAtividade.obj.atividade.titulo}" id="titulo" /></td>
			</tr>
			<tr>
				<th><b>Coordenação:</b></th>
				<td><h:outputText value="#{inscricaoAtividade.obj.atividade.coordenacao.servidor.pessoa.nome}" id="coord" /></td>
			</tr>
			<tr>
				<th><b>Período:</b></th>
				<td>
					<h:outputText value="#{inscricaoAtividade.obj.atividade.dataInicio}" id="inicio"> 
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
					até 
					<h:outputText value="#{inscricaoAtividade.obj.atividade.dataFim}" id="fim">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
				</td>
			</tr>
		</table>
<br/>


    <div class="infoAltRem">
        <h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;" id="labViewInscricao"/> Visualizar Inscrição
        <h:graphicImage value="/img/email_go.png" style="overflow: visible;" id="labReenviarInscricao"/>: Reenviar Senha de Acesso
    </div>

	<c:set value="#{inscricaoAtividade.inscricoesNaoConfirmadas}" var="naoConfirmados" />
	
<h:form id="form">
	<table class="listagem" width="100%">
			<caption>Lista das Inscrições nessa Ação</caption>
			<thead>
				<tr>
					<th style="text-align:center" width="8%">
						<a id="marcar" href="javascript:void(0)" onclick="marcarTodosCheckboxes();">TODOS</a>
					</th>
					<th width="10%" style="text-align: center;">Data do Cadastro</th>
					<th width="30%">Nome Completo</th>
					<th width="20%">E-mail</th>
					<th width="20%">Instituição</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				
				<tr>
					<td colspan="9" class="subFormulario">Inscrições PENDENTES (${fn:length(naoConfirmados)})</td>
				</tr>
				<c:if test="${empty naoConfirmados}">
					<tr>
						<td colspan="9">Não existem inscrições <b>pendentes de confirmação</b> nesta ação</td>
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
								<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
								
								<td width="2%">
									<h:commandLink title="Visualizar inscrição" immediate="true" id="viewInformacoesAceito"
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
							<h:commandButton value="Confirmar Inscrição" action="#{inscricaoAtividade.confirmaInscricaoParticipantes}" id="enviar" />
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