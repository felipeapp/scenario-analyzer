<%@include file="/public/include/cabecalho.jsp" %>


<script type="text/javascript" src="/sigaa/javascript/biblioteca/mostraInformacoesCompletaTitulo.js"> </script>
<script type="text/javascript" src="/sigaa/javascript/biblioteca/mostraPaginaDetalhesTodosItens.js"> </script>
<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js"> </script>

<style>
	table tbody tr th, table th {font-weight:bold;}
	table.listagem th.ano,.centro,.depto{text-align: left;}
	table.listagem th.ano{background: #C8D5EC;font-weight: bold;padding-left: 5px;border-bottom:#A4BFEF 1px solid;}
	table.listagem th.centro{background: #DFDFDF;font-weight: bold;padding-left: 5px;border-bottom:#CFCFCF 1px solid;}
	table.listagem th.depto{ background:#EFEFEF;font-weight: normal;padding-left: 5px;margin-top:5px;border-bottom:#CCC 1px solid;}
	table.listagem tr td{font-style:oblique;font-size: 0.9em;padding:0px;margin:0px;border-top:#CCC 1px dashed;}
	table.listagem tr td.titulo{padding-left: 15px;}
	
	
</style>
<f:view>

	<h2>
		 Novas Aquisições
	</h2>

	<div class="descricaoOperacao"> 
		<p>Essa página lista os livros adquiridos por compra e recebidos pelo <b>Sistema de Bibliotecas da ${ configSistema['siglaInstituicao'] }</b>.</p>
		<p>Para saber se o livro já se encontra disponível no acervo das bibliotecas, 
		<a href="/sigaa/public/biblioteca/buscaPublicaAcervo.jsf?aba=p-biblioteca">clique aqui</a>
		</p>
	</div>
	
	<h:form>



		<%--       formulario com os dados da busca     --%>



		<table id="tableDadosPesquisa" class="formulario" width="55%" style="margin-bottom: 20px">

			<caption>Selecione os campos para a busca</caption>
		
			<tbody>

				<tr>
					<th  width="25%">
						Título:
					</th>
			
					<td  width="75%" colspan="6">
						<h:inputText value="#{pesquisaPublicaBibliotecaMBean.titulo}" size="60"> </h:inputText>
					</td>
				</tr>
	
				<tr>
					<th  width="25%">
						Autor:
					</th>
			
					<td  width="75%" colspan="6">
						<h:inputText value="#{pesquisaPublicaBibliotecaMBean.autor}" size="60"> </h:inputText>
					</td>
				</tr>
	
				<tr>
					<th colspan="1" width="25%">
						Ano da Aquisição:
					</th>
			
					<td colspan="1" width="75%">
						<h:inputText value="#{pesquisaPublicaBibliotecaMBean.anoInicial}" size="7"  maxlength="4" onkeypress="mascara(this, soNumeros);"> </h:inputText>
					</td>
	
			
				</tr>

			</tbody>
	
			<tfoot>
				<tr>
			    	<td colspan="7">		
						<h:commandButton value="Pesquisar" actionListener="#{pesquisaPublicaBibliotecaMBean.pesquisarAquisicaoSIPAC}" />
						<h:commandButton value="Limpar" actionListener="#{pesquisaPublicaBibliotecaMBean.limpar}" />
					</td>
			    </tr>
			</tfoot>

		</table>
		
		
		<c:set var="aquisicao" value="#{pesquisaPublicaBibliotecaMBean.aquisicaoSIPAC}"/>
		<c:choose>
			<c:when test="${not empty aquisicao}">
			<table class="listagem"   style="margin-bottom: 20px;width: 90%;">
				<caption>  Títulos Encontrados ( ${fn:length(aquisicao)} )</caption>
				<thead>
					<tr>
						<th> Detalhes do Livro	</th>
						<th width="30%"> Autor </th>
						<th style="width: 2%; text-align: center"> Quant. </th>
					</tr>
				</thead>
				<tbody>
						<c:set var="anoAnterior" value=""/>
						<c:set var="centroAnterior" value=""/>
						<c:set var="deptoAnterior" value=""/>
						
						<c:forEach items="#{aquisicao}" var="aq" varStatus="status">
							<c:if test="${aq[7]!=anoAnterior}">
								
								<c:if test="${!status.first}">
								<tr>
									<th height="10px" ></th>
								</tr>
								</c:if>
								<tr>
									<th colspan="3" class="ano">Ano da Aquisição: <h:outputText value="#{aq[7]}"/></th>
								</tr>
							</c:if>
							<c:if test="${aq[4]!=centroAnterior}">
								<tr>
									<th colspan="3" class="centro"><h:outputText value="#{aq[4]}"/></th>
								</tr>
							</c:if>
							<c:if test="${aq[6]!=deptoAnterior}">
								<tr>
									<th colspan="3" class="depto"><h:outputText value="#{aq[6]}"/></th>
								</tr>
							</c:if>
							
							<tr>
								<td class="titulo">
									<h:outputText value="#{aq[0]}"/> 
									- Vol. 
									<h:outputText value="#{aq[1]}"/>
									,&nbsp;<h:outputText value="#{aq[2]}"/>
								</td>
								<td><h:outputText value="#{aq[3]}"/></td>
								<td align="center"><h:outputText value="#{aq[5]}"/>&nbsp; </td>
							</tr>
							
							<c:set var="anoAnterior" value="#{aq[7]}"/>
							<c:set var="centroAnterior" value="#{aq[4]}"/>
							<c:set var="deptoAnterior" value="#{aq[6]}"/>
						</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="10" style="text-align: center; font-weight: bold;">
						${fn:length(aquisicao)} título(s) encontrado(s).
						</td>
					</tr>
				</tfoot>
			</table>
			</c:when>
			
			<%-- Nao teve resultados --%>
			<c:otherwise>
	
			</c:otherwise>
			</c:choose>
			
			<%@include file="/public/include/voltar.jsp"%>
			
			</h:form>
</f:view>


<%@include file="/public/include/rodape.jsp" %>