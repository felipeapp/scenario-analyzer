<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Configurações das Unidades para o Módulo de Pesquisa </h2>

	<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:form>
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
					<h:commandLink action="#{siglaUnidadePesquisaMBean.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
				</h:form>
			</div>
	</center>

	<h:form>
		<table class="formulario" width="100%">
		<caption>Lista das Unidades Cadastradas</caption>
			
			<thead>
				<tr>
					<td style="text-align: center;">Sigla</td>
					<td>Unidade</td>
					<td>Unidade Classificação</td>
					<td>Unidade do CIC</td>
					<td></td>
					<td></td>
				</tr>
			</thead>
			
			<c:forEach var="siglas" items="#{ siglaUnidadePesquisaMBean.all }"  varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td style="text-align: center;">${ siglas.sigla }</td>
					<td>${ siglas.unidade.nome }</td>
					<td>${ siglas.unidadeClassificacao.nome }</td>
					<td>${ siglas.unidadeCic.nome }</td>
					
					<td width="20">
						<h:commandLink action="#{siglaUnidadePesquisaMBean.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar"/>
							<f:param name="id" value="#{siglas.id}"/>
						</h:commandLink>
					</td>

					<td width="20">
						<h:commandLink action="#{siglaUnidadePesquisaMBean.remover}" onclick="#{confirmDelete}" >
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
							<f:param name="id" value="#{siglas.id}"/>
						</h:commandLink>
					</td>
					
				</tr>
			</c:forEach>	
		
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>