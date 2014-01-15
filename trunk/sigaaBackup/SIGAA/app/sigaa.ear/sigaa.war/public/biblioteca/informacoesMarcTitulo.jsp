<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<%-- Pagina PUBLICA que mostra os dados marc do Título  --%>

<h2>  Dados MARC do Título Selecionado </h2>


<script type="text/javascript">
	function exibirProximaAba(idTitulo, nomeAba){
		window.location = '${ctx}/public/biblioteca/informacoesMarcTitulo.jsf?idTitulo='+idTitulo+'&'+nomeAba+'=true';
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
		margin-right:15%;               /* separa um do outros 20% */
		margin-left:15%;               /* separa um do outros 20% */
		 
	}

	a.linkSimulaAbasSelecionadas{
		margin-right:15%;               /* separa um do outros 20% */
		margin-left:15%;               /* separa um do outros 20% */
		color: red; 
	}
	
	-->
</style>


${geraInformacoesBibliograficasTituloMBean.gerarInformacoesTitulo} 

<f:view>

	<div class="divlinkSimulaAbas">
	
		
		<c:if test="${! requestScope.exibirDadosMarcPaginaPublica}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarc' );">
				MARC
			</a>		
		</c:if>
		
		<c:if test="${requestScope.exibirDadosMarcPaginaPublica}">
			<a class="linkSimulaAbasSelecionadas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarc' );">
				MARC
			</a>		
		</c:if>
		
		<c:if test="${! requestScope.exibirDadosMarcPublicoPaginaPublica}">
			<a class="linkSimulaAbas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarcPublico' );">
				MARC Público
			</a>
		</c:if>
		
		<c:if test="${requestScope.exibirDadosMarcPublicoPaginaPublica}">
			<a class="linkSimulaAbasSelecionadas" href="javascript: void(0);" onclick="exibirProximaAba(${geraInformacoesBibliograficasTituloMBean.obj.id}, 'exibirPaginaDadosMarcPublico' );">
				MARC Público
			</a>
		</c:if>
		
		
		
	</div>                
		
	<hr width="80%" />
		




		<%-- Testa quais ou como as informações do título serão exibidas. --%>


		<c:if test="${requestScope.exibirPaginaDadosMarc}">

			<%@include file="/public/biblioteca/dadosMarcTitulo.jsp"%>

		</c:if>

		<c:if test="${requestScope.exibirPaginaDadosMarcPublico}">

			<%@include file="/public/biblioteca/dadosMarcPublicoTitulo.jsp"%>

		</c:if> 

</f:view>

<%-- 
<p style="margin-top:50px; margin-bottom:10px; border: 1px #DEDEDE solid; background-color: #F5F5F5;">
	Quer saber mais sobre o formato MARC ?<br/><br/>
	<a href="http://www.loc.gov/marc/" target="_black">The Library of Congress</a><br/><br/>
	<a href="http://www.dbd.puc-rio.br/MARC21/" target="_black">PUC-RIO, divisão de bibliotecas e documentação</a>
</p> --%>



<%@include file="/biblioteca/rodape_popup.jsp"%>