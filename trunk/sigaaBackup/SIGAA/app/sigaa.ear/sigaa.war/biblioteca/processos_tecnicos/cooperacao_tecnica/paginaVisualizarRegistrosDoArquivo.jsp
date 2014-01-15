<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>



<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}">
<h2>  <ufrn:subSistema /> &gt; Dados do arquivo de T�tulos </h2>
</c:if>

<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
<h2>  <ufrn:subSistema /> &gt; Dados do arquivo de Autoridades </h2>
</c:if>


<div class="descricaoOperacao">
	<p>Nesta p�gina � poss�vel visualizar os dados contidos no arquivo MARC antes de salv�-los no sistema.</p> 
</div>


<f:view>

	<a4j:keepAlive beanName="cooperacaoTecnicaImportacaoMBean"></a4j:keepAlive>

	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

	<%-- Para o usu�rio usar o bot�o voltar e as informa��o da pesquisa dos t�tulos ainda est� l�. --%>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

	<%-- Mant�m as informa��o sobre a cataloga��o .--%>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
		<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	</c:if>
	
	<h:form>
	
		<table class="visualizacao" style="width: 900px;">
		
			<caption class="listagem">Registros contidos no Arquivo</caption>
		
			<tr>
				<td>
					${cooperacaoTecnicaImportacaoMBean.dadosDoArquivo}
				</td>
			</tr>
				 
			
			<tfoot>
				<tr>
					<td style="text-align: center;">
						<h:commandButton value="Importar" action="#{cooperacaoTecnicaImportacaoMBean.realizarInterpretacaoDados}" />
						<h:commandButton value="<< Voltar" action="#{cooperacaoTecnicaImportacaoMBean.telaImportarTitulo}" />
						<h:commandButton value="Cancelar" action="#{cooperacaoTecnicaImportacaoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>