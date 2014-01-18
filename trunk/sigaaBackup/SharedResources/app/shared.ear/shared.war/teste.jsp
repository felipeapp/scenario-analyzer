<%@page import="br.ufrn.arq.web.jsf.ArvoreUnidade"%>

<html>

<head>

<link rel="stylesheet" type="text/css" href="/shared/javascript/ext-1.1/resources/css/ext-all.css" />
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/ext/ext-base.js"> </script>
<script type="text/javascript" src="/shared/javascript/ext-1.1/ext-all.js"> </script>
<script type="text/javascript" src="/shared/javascript/treeFromList.js"> </script>

</head>

<body>

<%--
<div id="teste">
	<ul>
		<li><label>WQertyu</label></li>
		<li><label>frewver</label>
			<ul>
				<li><label>fewhfewhio</label></li>
			</ul>
		</li>
	</ul>
</div>
 --%>

<% ArvoreUnidade arvore = new ArvoreUnidade(); %>


<div id="arvore" style="display: none">
	<%= arvore.getArvoreUnidadeOrganizacional() %>
</div>

<div style="width:600; height: 400;">
Digite o código: <input type="text" name="codigo" size="5"/>
Buscar por Nome: <input type="text" name="nome" size="40"/> <br>

Escolhido: <input type="text" name="unEscolhida" size="40" id="unEscolhida"/>

<hr>

	<div id="arvoreRend" style="width:600; height: 350; overflow: auto">
		<%-- renderizado pelo ext --%>
	</div>

</div>

<script type="text/javascript">
	ListaArvore.init('arvore', 'arvoreRend');
</script>

</body>

</html>