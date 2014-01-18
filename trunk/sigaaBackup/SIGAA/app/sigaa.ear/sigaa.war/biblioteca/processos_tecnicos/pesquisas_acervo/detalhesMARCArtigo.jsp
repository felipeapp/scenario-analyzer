<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<%-- Pagina que abre em um popup para mostrar as informacoes completas de um titulo      --%>
<%-- Em quatro formatos diferentes: MARC, MARC public, Referencia e Ficha Catalografica  --%>

<h2>  Dados Marc do Artigo </h2>

<script type="text/javascript">
	function mostraProximaAba(idArtigo, nomeAba){
		window.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesMARCArtigo.jsf?idArtigo='+idArtigo+'&'+nomeAba+'=true';
	}
	
</script>



<style type="text/css">
	
	<!--
	
	/* o estilo do div centralizado a 20% das margens */
	
	.divlinkSimulaAbas{
		margin-left:10%; 
		margin-right:10%;
		margin-top:30px;
	}

	/* o estilo dos links dentro do div (tenta imitar uma aba) */

	.linkSimulaAbas{
		margin-right:15%;               /* separa um do outros 15% */
		margin-left:15%;               /* separa um do outros 15% */
	}

	a.linkSimulaAbasSelecionada{
		margin-right:15%;               /* separa um do outros 15% */
		margin-left:15%;               /* separa um do outros 15% */
		color: red;
	}

	-->
</style>
                     

${detalhesMARCMBean.carregaInformacoesMarcArtigo} 

<f:view>
	
	<%--
	<div id="abas">
		<div id="marc" class="aba">
			<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesAutoridadeFormatoMarc.jsp"%>
		</div>
		<div id="marcPublico" class="aba">
			<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesAutoridadeFormatoMarcPublico.jsp"%>
		</div>
	</div> --%>


	<div class="divlinkSimulaAbas">
		
		<c:if test="${! requestScope.mostarPaginaDadosMarc}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="mostraProximaAba(${detalhesMARCMBean.obj.id}, 'mostarPaginaDadosMarc' );">
				MARC
			</a>		
		</c:if>

		<c:if test="${requestScope.mostarPaginaDadosMarc}">
			<a class="linkSimulaAbasSelecionada" href="javascript: void(0);" onclick="mostraProximaAba(${detalhesMARCMBean.obj.id}, 'mostarPaginaDadosMarc' );">
				MARC
			</a>
		</c:if>

		<c:if test="${! requestScope.mostarPaginaDadosMarcPublico}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="mostraProximaAba(${detalhesMARCMBean.obj.id}, 'mostarPaginaDadosMarcPublico' );">
				MARC P�blico
			</a>
		</c:if>
		
		<c:if test="${requestScope.mostarPaginaDadosMarcPublico}">
			<a class="linkSimulaAbasSelecionada" href="javascript: void(0);" onclick="mostraProximaAba(${detalhesMARCMBean.obj.id}, 'mostarPaginaDadosMarcPublico' );">
				MARC P�blico
			</a>
		</c:if>

	</div>                 
		
	<hr width="80%" />
		




	<%-- Testa quais ou como as informa��es do t�tulo ser�o mostradas --%>



	<c:if test="${requestScope.mostarPaginaDadosMarc}">

		<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesArtigoFormatoMarc.jsp"%>

	</c:if>

	<c:if test="${requestScope.mostarPaginaDadosMarcPublico}">

		<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesArtigoFormatoMarcPublico.jsp"%>

	</c:if>


</f:view>


<%@include file="/biblioteca/rodape_popup.jsp"%>