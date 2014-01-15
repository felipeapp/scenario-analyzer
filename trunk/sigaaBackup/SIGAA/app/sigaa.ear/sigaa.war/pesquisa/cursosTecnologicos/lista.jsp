<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Cursos Tecnológicos</h2>
	<h:outputText value="#{cotaBolsasMBean.create}" />
	
	<center>
			<div class="infoAltRem">
				<h:form id="formLegenda">
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{cursosTecnologicosMBean.preCadastrar}" value="Cadastrar"/>&nbsp;
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar&nbsp;
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
				</h:form>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista dos Cursos Tecnológicos</caption>
		<thead>
		<tr>
			<td>Curso</td>
			<td style="text-align: center;">Data Cadastro</td>
			<td>Cadastro por</td>
			<td></td>
			<td></td>
		</tr>
		</thead>
		<h:form id="formListagem">
		<c:forEach items="#{cursosTecnologicosMBean.allPaginado}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${ item.curso.nomeCompleto } </td>
				<td style="text-align: center;">				
					<h:outputText value="#{ item.dataCadastro }" rendered="#{ empty item.dataAtualizacao }">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>

					<h:outputText value="#{ item.dataAtualizacao }" rendered="#{ not empty item.dataAtualizacao }">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
				</td>
				<td> 
					<h:outputText value="#{ item.registroCadastro.usuario.pessoa.nome }" rendered="#{ empty item.registroAtualizacao }" />
					<h:outputText value="#{ item.registroAtualizacao.usuario.pessoa.nome }" rendered="#{ not empty item.registroAtualizacao }" />
				</td>
				<td width="20">
					<h:commandLink title="Alterar" action="#{cursosTecnologicosMBean.atualizar}">
						<f:param name="id" value="#{item.id}"/>
						<h:graphicImage url="/img/alterar.gif"/>
					</h:commandLink>
				</td>
				<td width="25">
					<h:commandLink title="Remover" action="#{cursosTecnologicosMBean.remover}" onclick="#{confirmDelete}">
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