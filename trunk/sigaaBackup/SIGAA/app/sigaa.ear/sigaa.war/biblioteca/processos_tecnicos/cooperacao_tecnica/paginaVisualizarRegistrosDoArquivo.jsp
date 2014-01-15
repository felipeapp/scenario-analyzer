<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>



<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoBibliografica}">
<h2>  <ufrn:subSistema /> &gt; Dados do arquivo de Títulos </h2>
</c:if>

<c:if test="${cooperacaoTecnicaImportacaoMBean.cooperacaoAutoridades}">
<h2>  <ufrn:subSistema /> &gt; Dados do arquivo de Autoridades </h2>
</c:if>


<div class="descricaoOperacao">
	<p>Nesta página é possível visualizar os dados contidos no arquivo MARC antes de salvá-los no sistema.</p> 
</div>


<f:view>

	<a4j:keepAlive beanName="cooperacaoTecnicaImportacaoMBean"></a4j:keepAlive>

	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

	<%-- Para o usuário usar o botão voltar e as informação da pesquisa dos títulos ainda está lá. --%>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

	<%-- Mantém as informação sobre a catalogação .--%>
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