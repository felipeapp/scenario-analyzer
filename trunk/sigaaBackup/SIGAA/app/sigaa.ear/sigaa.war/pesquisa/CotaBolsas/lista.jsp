<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Cota de Bolsas</h2>
	<h:outputText value="#{cotaBolsasMBean.create}" />
	
	<center>
			<div class="infoAltRem">
				<h:form id="formLegenda">
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{cotaBolsasMBean.preCadastrar}" value="Cadastrar"/>&nbsp;
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar&nbsp;
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
				</h:form>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Cotas de Bolsas</caption>
		<thead>
		<tr>
			<td>Código</td>
			<td>Descrição</td>
			<td>Período de Validade</td>
			<td>Órgão Financiador</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form id="formListagem">
		<c:forEach items="#{cotaBolsasMBean.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.codigo}</td>
				<td>${item.descricao}</td>
				<td>
					<h:outputText value="#{item.inicio}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> a 
					<h:outputText value="#{item.fim}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText> 
				</td>
				<td>${item.entidadeFinanciadora.nome}</td>
				<td width="20">
					<h:commandLink title="Alterar" action="#{cotaBolsasMBean.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="25">
					<h:commandLink title="Remover" action="#{cotaBolsasMBean.inativar}" onclick="#{confirmDelete}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/delete.gif"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</h:form>
	</table>
	
	
	<center>
	<h:form id="formPaginacao">
		<h:messages showDetail="true"/>
		<h:graphicImage value="/img/voltar_des.gif" style="overflow: visible;" rendered="#{paginacao.primeiraPagina}"/>
		<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{!paginacao.primeiraPagina}"/>
		<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true">
			<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
		</h:selectOneMenu>
		<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}" rendered="#{!paginacao.ultimaPagina}"/>
		<h:graphicImage value="/img/avancar_des.gif" style="overflow: visible;" rendered="#{paginacao.ultimaPagina}"/>
	</h:form>
	</center>
	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
