<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<script type="text/javascript">

	//função para abrir a página dos dados MARC sempre na mesma janela do navegador
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
		<h2>  <ufrn:subSistema /> &gt; Cadastro de Materiais  &gt; Incluir Fascículo </h2>
		
		<div class="descricaoOperacao"> 
			<p>Caro usuário,</p>
			<p>Nesta página é possível incluir no acervo os fascículos para a Catalogação selecionada no passo anterior.</p>
			<p>Abaixo são mostradas duas listagem:
				<ul>
					<li>Uma listagem com as assinaturas que estão associadas com a catalogação escolhida.</li>
					<li>Outra listagem de assinaturas que foram criadas mas ainda não estão associadas a nenhuma catalogação.</li>
				</ul>
				Observação: Caso seja incluído no acervo um fascículo de alguma assinatura da segunda listagem, ela será automaticamente associada à Catalogação selecionada.
			</p>
			</div>
		
	</c:if>
	<br>

	<h:form id="fromIncluirItem">
		
		<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
		
		<%-- Para manter os dados quando é importado vários título e incluído materiais --%>
		<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
		
		<div style="margin-bottom: 20px; text-align: center; margin-left: auto; margin-right: auto; width: 100%">
			<table class="visualizacao">
				<tr>
					<th colspan="2" style="text-align: center">Catalogação: </th>
				</tr>
				<tr>
					<td> ${materialInformacionalMBean.tituloEmFormatoReferencia} </td>
					<td>
						<a href="#buscarTitulo" onclick="abreJanelaInformacoesCompletasTitulo(${materialInformacionalMBean.titulo.id})">
							<img src="${ctx}/img/biblioteca/visualizarMarc.png" title="Visualizar as Informações MARC do Título"/>
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
		
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
					
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>