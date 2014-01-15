<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<script type="text/javascript">

	//fun��o para abrir a p�gina dos dados MARC sempre na mesma janela do navegador
	var janela = null;
	
	function abreJanelaInformacoesCompletasTitulo(idTitulo){
		if (janela == null || janela.closed){
			janela = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesMARCTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true','','width=1024,height=500,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesMARCTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true';
		}
	
		janela.focus();
	}

	
</script>


<f:view>	
	<c:if test="${materialInformacionalMBean.inclusaoExemplares}">
		<h2>  <ufrn:subSistema /> &gt; Cadastro de Materiais  &gt; Incluir Exemplar </h2>
	</c:if>
	<c:if test="${materialInformacionalMBean.inclusaoFasciculos }">
		<h2>  <ufrn:subSistema /> &gt; Cadastro de Materiais  &gt; Incluir Fasc�culo </h2>
		
		<div class="descricaoOperacao"> 
			<p>Caro usu�rio,</p>
			<p>Nesta p�gina � poss�vel incluir no acervo os fasc�culos para a Cataloga��o selecionada no passo anterior.</p>
			<p>Abaixo s�o mostradas duas listagem:
				<ul>
					<li>Uma listagem com as assinaturas que est�o associadas com a cataloga��o escolhida.</li>
					<li>Outra listagem de assinaturas que foram criadas mas ainda n�o est�o associadas a nenhuma cataloga��o.</li>
				</ul>
				Observa��o: Caso seja inclu�do no acervo um fasc�culo de alguma assinatura da segunda listagem, ela ser� automaticamente associada � Cataloga��o selecionada.
			</p>
			</div>
		
	</c:if>
	<br>

	<h:form id="fromIncluirItem">
		
		<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
		
		<%-- Para manter os dados quando � importado v�rios t�tulo e inclu�do materiais --%>
		<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
		
		<div style="margin-bottom: 20px; text-align: center; margin-left: auto; margin-right: auto; width: 100%">
			<table class="visualizacao">
				<tr>
					<th colspan="2" style="text-align: center">Cataloga��o: </th>
				</tr>
				<tr>
					<td> ${materialInformacionalMBean.tituloEmFormatoReferencia} </td>
					<td>
						<a href="#buscarTitulo" onclick="abreJanelaInformacoesCompletasTitulo(${materialInformacionalMBean.titulo.id})">
							<img src="${ctx}/img/biblioteca/visualizarMarc.png" title="Visualizar as Informa��es MARC do T�tulo"/>
						</a>
					</td>
				</tr>
			</table>
		</div>
		
		<c:if test="${ materialInformacionalMBean.inclusaoExemplares }">
			<%@include file="/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoExemplares.jsp"%>
		</c:if>

		<c:if test="${ materialInformacionalMBean.inclusaoFasciculos }">
			<%@include file="/biblioteca/processos_tecnicos/catalogacao/paginaInclusaoFasciculos.jsp"%>
		</c:if>
		
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
					
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>