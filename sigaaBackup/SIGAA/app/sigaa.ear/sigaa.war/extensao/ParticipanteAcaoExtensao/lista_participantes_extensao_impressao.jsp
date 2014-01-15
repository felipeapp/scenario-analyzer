<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>

	<a4j:keepAlive beanName="listaAtividadesParticipantesExtensaoMBean" />

	<h2>Lista de Participantes de Ações de Extensão</h2>
	
	
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
	
	<table class="tabelaRelatorio" width="100%">
		<caption>Lista de Participantes</caption>
		<thead>
			<tr>
				<th align="right">Nº</th>
				<th style="text-align: center;">CPF</th>
				<th style="text-align: center;">Passaporte</th>
				<th>Nome</th>
				<th>Data de Nascimento</th>
				<th>E-mail</th>
				<th style="text-align: center;">Tipo de Participação</th>								
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{listaAtividadesParticipantesExtensaoMBean.participantes}" var="participante" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td style="text-align: right;"> ${status.count}</td>
					<td style="text-align: center; width: 15%;">
						
						<c:if test="${participante.cadastroParticipante.cpf <= 0 || participante.cadastroParticipante.cpf == null}"> - </c:if>
						<c:if test="${participante.cadastroParticipante.cpf > 0}"><ufrn:format type="cpf_cnpj" valor="${participante.cadastroParticipante.cpf}" /> </c:if>
						
					</td>					
					<td style="text-align: center; width: 15%;">
						
						<c:if test="${participante.cadastroParticipante.passaporte == null}"> - </c:if>
						<c:if test="${participante.cadastroParticipante.passaporte != null}">${participante.cadastroParticipante.passaporte}</c:if>
						
					</td>
					<td>${participante.cadastroParticipante.nome}</td>
					<td> <ufrn:format type="data" valor="${participante.cadastroParticipante.dataNascimento}" /> </td>
					<td> ${participante.cadastroParticipante.email} </td>
					<td style="text-align: center;">${participante.tipoParticipacao.descricao}</td>
				</tr>			
			</c:forEach>
			<c:if test="${listaAtividadesParticipantesExtensaoMBean.qtdParticipantes <= 0 }">
                <tr>
                   <td colspan="4" style="text-align: center; color=red;">Não há participantes cadastrados para esta ação de extensão.</td>
                </tr>
            </c:if>
		</tbody>		
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>