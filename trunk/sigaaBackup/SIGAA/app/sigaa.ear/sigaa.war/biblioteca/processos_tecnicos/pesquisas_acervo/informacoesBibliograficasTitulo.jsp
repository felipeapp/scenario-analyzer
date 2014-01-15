<%@include file="/biblioteca/cabecalho_popup.jsp"%>


<%-- Pagina popup que mostra as informa��es bibliografica de um Titulo em quatro formatos diferentes: Referencia e Ficha Catalografica  --%>





<h2 class="naoImprimir">  Informa��es Bibliogr�ficas do T�tulo </h2>

<script type="text/javascript">
	function exibirProximaAba(idTitulo, nomeAba){
		window.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesBibliograficasTitulo.jsf?idTitulo='+idTitulo+'&'+nomeAba+'=true';
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

		<c:if test="${! requestScope.exibirPaginaReferencias}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaReferencias' );">
				Refer�ncias
			</a>
		</c:if>
		
		<c:if test="${requestScope.exibirPaginaReferencias}">
			<a class="linkSimulaAbasSelecionadas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaReferencias' );">
				Refer�ncias
			</a>
		</c:if>
		
		<c:if test="${! requestScope.exibirPaginaFichaCatalografica}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaFichaCatalografica' );">
				Ficha Catalogr�fica
			</a>
		</c:if>

		<c:if test="${requestScope.exibirPaginaFichaCatalografica}">
			<a class="linkSimulaAbasSelecionadas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaFichaCatalografica' );">
				Ficha Catalogr�fica
			</a>
		</c:if>

		
	</div>                
		
	<hr width="80%" class="naoImprimir"/>
		




		<%-- Testa quais ou como as informa��es do t�tulo ser�o exibidas. --%>

		<c:if test="${requestScope.exibirPaginaReferencias}">

			<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/exibeFormatoReferenciaTitulo.jsp"%>

		</c:if>

		<c:if test="${requestScope.exibirPaginaFichaCatalografica}">

			<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/exibeFichaCatalograficaTitulo.jsp"%>

		</c:if>

		

</f:view>

<div class="naoImprimir">
<%@include file="/biblioteca/rodape_popup.jsp"%>
</div>


