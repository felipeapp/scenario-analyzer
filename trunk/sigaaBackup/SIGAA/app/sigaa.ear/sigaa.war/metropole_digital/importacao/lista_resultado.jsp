<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/porta_arquivos.css" type="text/css" />

<f:view>

	<h2><ufrn:subSistema /> &gt; Importar Discentes IMD </h2>

	<h:form>
	
		<c:if test="${not empty importacaoDiscenteIMDMBean.listaSucessos}">
			<table class=listagem style="width:100%">
				<caption class="listagem">Lista de importação realizadas com sucesso (${fn:length(importacaoDiscenteIMDMBean.listaSucessos)})</caption>
				<thead>
					<tr>
						<td>Nº Inscrição</td>
						<td>Nome</td>
						<td>CPF</td>
						<td>Opção - Polo - Grupo</td>
					</tr>
				</thead>
				<h:form>
				<c:forEach items="#{importacaoDiscenteIMDMBean.listaSucessos}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
						<td>${item.numeroInscricao}</td>
						<td>${item.pessoa.nome}</td>
						<td><ufrn:format type="cpf_cnpj" valor="${item.pessoa.cpf_cnpj}" /></td>
						<td>${item.opcao.descricao}</td>
					</tr>
				</c:forEach>
				</h:form>
			</table>
			<br />	
		</c:if>
		
		<c:if test="${not empty importacaoDiscenteIMDMBean.listaErros}">
			<table class=listagem style="width:100%">
				<caption class="listagem">Lista de importação que NÃO foram realizadas (${fn:length(importacaoDiscenteIMDMBean.listaErros)})</caption>
				<thead>
					<tr>
						<td>Nº Inscrição</td>
						<td>Nome</td>
						<td>CPF</td>
						<td>Opção - Polo - Grupo</td>
					</tr>
				</thead>
				<h:form>
				<c:forEach items="#{importacaoDiscenteIMDMBean.listaErros}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
						<td>${item.numeroInscricao}</td>
						<td>${item.pessoa.nome}</td>
						<td><ufrn:format type="cpf_cnpj" valor="${item.pessoa.cpf_cnpj}" /></td>
						<td>${item.opcao.descricao}</td>
					</tr>
				</c:forEach>
				</h:form>
			</table>
			<br />	
		</c:if>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>