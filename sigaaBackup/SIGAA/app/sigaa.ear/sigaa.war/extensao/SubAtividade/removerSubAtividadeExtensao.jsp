<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.ajuda {
	text-align: center;
	margin: 5px 90px;
	color: #083772;
	font-style: italic;
}
div#msgAddProcessos{
	background:#FFF none repeat scroll 0 0;
	border:1px solid #FFDFDF;
	color:#FF1111;
	line-height:1.2em;
	padding:10px;
	width:95%;
}
</style>

<f:view>
<h2><ufrn:subSistema /> > Alterar Informações da SubAtividade</h2>

<div class="descricaoOperacao">
	<p>Caro Usuário,</p>
	<p>Utilize essa tela para remover uma Mini Atividade do sistema.</p>
	<br/>
	<p> <strong>IMPORTANTE:</strong> Se a mini atividade tiver inscrições e ela for removida, as suas inscrições serão desatividas.</p>
	<br/>
	<p> <strong>Observação:</strong> Será enviado um email a todos os participantes informando que a mini atividade foi removida.</p>
	<br/>
</div>

	<%-- Fica guardando os itens gerados entre requisicoes  --%>
	<a4j:keepAlive beanName="subAtividadeExtensaoMBean" />
	
	<%-- Para quando é chamada a partir da tela que lista as atividades do usuário --%>
	<a4j:keepAlive beanName="atividadeExtensao" /> 

	<h:form id="formAlteraDadosSubAtividade">
	
		<table class=listagem style="width: 95%;">
			
			<caption>Mini Atividade Selecionada</caption>
			
			<tr>
				<td colspan="2" style="text-align: center; font-weight: bold;" > Atividade de Extensão: <h:outputText value="#{subAtividadeExtensaoMBean.obj.atividade.codigo}"/> - <h:outputText value="#{subAtividadeExtensaoMBean.obj.atividade.titulo}"/> </td>
			</tr>
			
			<tr>
				<th>Título:</th>
				<td><h:outputText value="#{subAtividadeExtensaoMBean.obj.titulo}"/></td>
			</tr>
			
			<tr>
				<th> Tipo do Curso:</th>
				<td>
					<h:outputText value="#{subAtividadeExtensaoMBean.obj.tipoSubAtividadeExtensao.descricao}" />
				</td>
			</tr>
			
			<tr>
				<th>Local:</th>
				<td><h:outputText value="#{subAtividadeExtensaoMBean.obj.local}" /></td>
			</tr>
			
			<tr>
			<th> Período: </th>
			<td>
				<h:outputText value="#{subAtividadeExtensaoMBean.obj.inicio}" />
				a 
				<h:outputText value="#{subAtividadeExtensaoMBean.obj.fim}"/>
			</td>
			
			<tr>
				<th>Horário:</th>
				<td><h:outputText value="#{subAtividadeExtensaoMBean.obj.horario}"/></td>
			</tr>
			
			<tr>
				<th width="35%"> Carga Horária:</th>
				<td>
					<h:outputText id="cargaHoraria" value="#{subAtividadeExtensaoMBean.obj.cargaHoraria}" /> horas
				</td>
			</tr>
			
			<tr>
				<th width="35%"> Vagas:</th>
				<td >
					<h:outputText value="#{subAtividadeExtensaoMBean.obj.numeroVagas}"/>				
				</td>
			</tr>
			
			<tr>
				<th width="35%"> Descrição:</th>
				<td> <h:outputText escape="true" value="#{subAtividadeExtensaoMBean.obj.descricao}"/> </td>
			</tr>
			
			<tr>
				<td colspan="4" class="subFormulario">Participantes Inscritos na Mini Atividade</td>
			</tr>
			
			<c:if test="${empty subAtividadeExtensaoMBean.participantes }">
				<tr>
					<td colspan="4" style="color: green; font-variant: small-caps; text-align: center; font-weight: bold;">
						Não existem inscrições para essa mini atividade. Ela pode ser removida com segurança
					</td>
				</tr>
			</c:if>
			
			<c:if test="${not empty subAtividadeExtensaoMBean.participantes }">
				<tr>
					<td colspan="4" style="color: red; font-variant: small-caps; text-align: center; font-weight: bold;">
						Existem inscrições realizadas para essa mini atividade. A sua remoção inativará essas inscrições.
					</td>
				</tr>
			</c:if>
			
			<c:if test="${not empty subAtividadeExtensaoMBean.participantes }">
				<c:forEach items="#{subAtividadeExtensaoMBean.participantes}" var="participante">
					<tr>
						<td>
						<h:outputText value="#{participante.cadastroParticipante.nome}" />
						</td>
						<td>
						<h:outputText value="#{participante.cadastroParticipante.email}" />
						</td>
						<td>
						<h:outputText value="#{participante.cadastroParticipante.dataNascimento}" />
						</td>
						<td>
						<h:outputText value="#{participante.statusInscricao.descricao}"/>
						</td>
					</tr>
				</c:forEach>
			</c:if>
			
			
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton id="cmdButtonRemoverSubAtividade" value="Remover Mini Atividade" action="#{subAtividadeExtensaoMBean.removerSubAtividade}" onclick="return confirm('Confirma a Remoção da Mini Atividade ?');" />
						<h:commandButton id="cmdButtonCancelar" value="<< Voltar" action="#{subAtividadeExtensaoMBean.cancelar}" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>