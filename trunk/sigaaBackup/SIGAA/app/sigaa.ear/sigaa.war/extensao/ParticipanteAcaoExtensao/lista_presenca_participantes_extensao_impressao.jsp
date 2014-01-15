<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>

	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />

	<h2>Lista de Participantes da A��o de Extens�o</h2>
		
	<div id="parametrosRelatorio">
		<c:if test="${listaAtividadesParticipantesExtensaoMBean.gerenciandoParticipantesAtividade}">
			<table>
				<tr>
					<th>C�digo:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.codigo}" id="codigo" /></td>
				</tr>
				<tr>
					<th>Atividade:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.titulo}" id="titulo" /></td>
				</tr>
				<tr>
					<th>Coordena��o:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.projeto.coordenador.pessoa.nome}" id="coord" /></td>
				</tr>
				<tr>
					<th>Per�odo:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.atividadeSelecionada.dataInicio}" id="inicio"> 
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText> 
						at� 
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
					<th>C�digo:</th>
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
					<th>Coordena��o:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.atividade.projeto.coordenador.pessoa.nome}" id="coord" /></td>
				</tr>
				<tr>
					<th>Per�odo:</th>
					<td><h:outputText value="#{listaAtividadesParticipantesExtensaoMBean.subatividadeSelecionada.inicio}" id="inicio"> 
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText> 
						at� 
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
		<h:outputText value="Lista de Presen�a dos Participantes" />
	</h3>
	<table class="tabelaRelatorio" border="1" style="width: 100%;">
		<thead>
			<tr>
				<th style="text-align: right; width: 5%">N�</th>
				<th style=" width: 15%">Identifica��o</th>
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
					<td> </td> <%-- Para o usu�rio assinar  --%>
				</tr>			
			</c:forEach>
            <c:if test="${listaAtividadesParticipantesExtensaoMBean.qtdParticipantes <= 0}">
                <tr>
                   <td colspan="4" style="text-align: center; color: red;"> N�o h� participantes cadastrados para esta a��o de extens�o. </td>
                </tr>
            </c:if>
		</tbody>		
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>