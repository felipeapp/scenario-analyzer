<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2>Qualifica��o</h2>

	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" />: 
				<h:commandLink action="#{qualificacaoDocente.preCadastrar}" value="Cadastrar Nova Qualifica��o">
			</h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Qualifica��o
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Qualifica��o <br/>
		</div>
	</h:form>

	<h:outputText value="#{qualificacaoDocente.create}" />

	<c:set var="lista" value="#{qualificacaoDocente.all}" />
	<c:if test="${empty lista}">
		<br />
		<center><span style="color: red;">Nenhuma Qualifica��o Encontrada.</span></center>
	</c:if>

	<c:if test="${not empty lista }">
		<h:outputText value="#{util.create}" />
		<table class="listagem" width="100%">
			<caption class="listagem">Lista de Qualifica��es</caption>
			<thead>
				<tr>
					<td><b>Qualifica��o</b></td>
					<td><b>Docente</b></td>
					<td><b>Orientador</b></td>
					<th>In�cio</th>
					<th>Conclus�o</th>
					<td></td>
					<td></td>
				</tr>
			</thead>
			<c:forEach items="#{qualificacaoDocente.allDepartamento}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaImpar' : 'linhaPar' }">
					<td>
						<c:if test="${item.qualificacao == 'G'}">
					 	Gradua��o
						</c:if> 
						<c:if test="${item.qualificacao == 'C'}">
						Curso de Atualiza��o Pedag�gica
						</c:if> 
						<c:if test="${item.qualificacao == 'E'}">
						Especializa��o
						</c:if> 
						<c:if test="${item.qualificacao == 'M'}">
						Mestrado
						</c:if> 
						<c:if test="${item.qualificacao == 'D'}">
						Doutorado
						</c:if> 
						<c:if test="${item.qualificacao == 'P'}">
						P�s-Doutorado
						</c:if> 
						<c:if test="${item.qualificacao == 'O'}">
						Outra
						</c:if>
					</td>
					<td>${item.servidor.nome}</td>
					<td>${item.orientador}</td>
					<td>
						<h:outputText value="#{ item.dataInicial }">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</td>
					<td>
						<h:outputText value="#{ item.dataFinal }">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</td>
					<h:form>
						<td width="20">
							<h:commandLink title="Alterar Qualifica��o" action="#{qualificacaoDocente.atualizar}">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</td>
					</h:form>
					<h:form>
						<td width="20">
							<h:commandLink title="Remover Qualifica��o" action="#{qualificacaoDocente.remover}"
								onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</td>
					</h:form>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
