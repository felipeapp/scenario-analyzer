<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>

	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />

	<h2>Lista dos contatos dos participantes da A��o de Extens�o</h2>
	
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
	
	<table class="tabelaRelatorio" style="width: 100%;">
		<caption>Lista de contatos</caption>
		<thead>
			<tr>
				<th style="width: 30%;">Nome</th>
				<th>Telefone</th>
				<th>Celular</th>
				<th>E-mail</th>
				<th>Endere�o</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{listaAtividadesParticipantesExtensaoMBean.participantes}" var="participante" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar'}">
					<td>${participante.cadastroParticipante.nome}</td>
					<td>${participante.cadastroParticipante.telefone}</td>
					<td>${participante.cadastroParticipante.celular}</td>
					<td>${participante.cadastroParticipante.email}</td>
					<td>${participante.cadastroParticipante.enderecoCompleto}</td>
				</tr>
			</c:forEach>
			<c:if test="${empty listaAtividadesParticipantesExtensaoMBean.qtdParticipantes}">
                <tr>
                   <td colspan="3" style="text-align: center; color: red;"> N�o h� participantes cadastrados para esta a��o de extens�o. </td>
                </tr>
            </c:if>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>