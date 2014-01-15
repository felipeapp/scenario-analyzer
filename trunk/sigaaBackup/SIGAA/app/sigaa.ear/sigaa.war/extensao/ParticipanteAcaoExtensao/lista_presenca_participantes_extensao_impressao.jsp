<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>

	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />

	<h2>Lista de Participantes da Ação de Extensão</h2>
		
	<div id="parametrosRelatorio">
		<c:if test="${listaAtividadesParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
			<table>
				<tr>
					<th>Código:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.codigo}" id="codigo" /></td>
				</tr>
				<tr>
					<th>Atividade:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.titulo}" id="titulo" /></td>
				</tr>
				<tr>
					<th>Coordenação:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.projeto.coordenador.pessoa.nome}" id="coord" /></td>
				</tr>
				<tr>
					<th>Período:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.dataInicio}" id="inicio"> 
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText> 
						até 
						<h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.dataFim}" id="fim">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText> 
					</td>
				</tr>
			
			</table>
		</c:if>
		
		<c:if test="${! listaAtividadesParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
			<table>
				<tr>
					<th>Código:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.atividade.codigo}" id="codigo" /></td>
				</tr>
				<tr>
					<th>Atividade:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.atividade.titulo}" id="titulo" /></td>
				</tr>
				<tr>
					<th>Mini Atividade:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.titulo}" id="tituloSubAtividade" /></td>
				</tr>
				<tr>
					<th>Coordenação:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.atividade.projeto.coordenador.pessoa.nome}" id="coord" /></td>
				</tr>
				<tr>
					<th>Período:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.inicio}" id="inicio"> 
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText> 
						até 
						<h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.fim}" id="fim">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText> 
					</td>
				</tr>
			
			</table>
		</c:if>
	</div>
	<br />
	<h3 class="tituloTabelaRelatorio">
		<h:outputText value="Lista de Presença dos Participantes" />
	</h3>
	<table class="tabelaRelatorio" border="1" style="width: 100%;">
		<thead>
			<tr>
				<th style="text-align: right; width: 5%">Nº</th>
				<th style=" width: 15%">Identificação</th>
				<th style=" width: 30%">Nome</th>
				<th style=" width: 50%">Assinatura</th>								
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{listaAtividadesParticipantesExtensaoMBean.participantes}" var="participante" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" style="height: 30px;">
					<td style="text-align: right;"> ${status.count}</td>
					<td>
						<c:if test="${participante.cadastroParticipante.cpf != null && participante.cadastroParticipante.cpf > 0}"> <ufrn:format type="cpf_cnpj" valor="${participante.cadastroParticipante.cpf}" /> </c:if>
						<c:if test="${participante.cadastroParticipante.cpf == null && participante.cadastroParticipante.passaporte != null && participante.cadastroParticipante.passaporte != ''}"> ${participante.cadastroParticipante.passaporte} </c:if>
					</td>
					<td>
						  <h:outputText value="#{participante.cadastroParticipante.nome}" />
					</td>
					<td> </td> <%-- Para o usuário assinar  --%>
				</tr>			
			</c:forEach>
            <c:if test="${listaAtividadesParticipantesExtensaoMBean.qtdParticipantes <= 0}">
                <tr>
                   <td colspan="4" style="text-align: center; color: red;"> Não há participantes cadastrados para esta ação de extensão. </td>
                </tr>
            </c:if>
		</tbody>		
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>