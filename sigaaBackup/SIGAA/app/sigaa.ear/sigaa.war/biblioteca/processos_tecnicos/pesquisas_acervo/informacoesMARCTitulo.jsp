<%@include file="/biblioteca/cabecalho_popup.jsp"%>


<%-- Pagina popup que mostra as informações bibliografica de um Titulo em quatro formatos diferentes: MARC, MARC public  --%>





<h2 class="naoImprimir">  Dados MARC do Título </h2>

<script type="text/javascript">
	function exibirProximaAba(idTitulo, nomeAba){
		window.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesMARCTitulo.jsf?idTitulo='+idTitulo+'&'+nomeAba+'=true';
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
		margin-right:5%;               /* separa um do outros 3% */
		margin-left:5%;               /* separa um do outros 3% */
		 
	}

	a.linkSimulaAbasSelecionadas{
		margin-right:5%;               /* separa um do outros 3% */
		margin-left:5%;               /* separa um do outros 3% */
		color: red; 
	}
	
	-->
</style>
                     

${geraInformacoesBibliograficasTituloMBean.gerarInformacoesTitulo} 

<f:view>


	<div class="divlinkSimulaAbas naoImprimir">
	
		<c:if test="${! requestScope.exibirPaginaDadosMarc}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarc' );">
				MARC
			</a>		
		</c:if>
		
		<c:if test="${requestScope.exibirPaginaDadosMarc}">
			<a class="linkSimulaAbasSelecionadas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarc' );">
				MARC
			</a>		
		</c:if>
		
		<c:if test="${! requestScope.exibirPaginaDadosMarcPublico}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarcPublico' );">
				MARC Público
			</a>
		</c:if>
		
		<c:if test="${requestScope.exibirPaginaDadosMarcPublico}">
			<a class="linkSimulaAbasSelecionadas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarcPublico' );">
				MARC Público
			</a>
		</c:if>
		
	</div>                
		
	<hr width="80%" class="naoImprimir"/>



	<%-- Testa quais ou como as informações do título serão exibidas. --%>



	<c:if test="${requestScope.exibirPaginaDadosMarc}">

		<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/exibeFormatoMarcTitulo.jsp"%>

	</c:if>

	<c:if test="${requestScope.exibirPaginaDadosMarcPublico}">

		<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/exibeFormatoMarcPublicoTitulo.jsp"%>

	</c:if>

</f:view>

<div class="naoImprimir">
<%@include file="/biblioteca/rodape_popup.jsp"%>
</div>
