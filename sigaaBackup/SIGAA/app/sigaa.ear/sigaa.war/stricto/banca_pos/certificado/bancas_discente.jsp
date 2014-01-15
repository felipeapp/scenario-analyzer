<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<div class="descricaoOperacao">Para uma
melhor impressão da declaração, configure seu navegador para não imprimir o cabeçalho e rodapé.
<ul>
<li>Para o Firefox, vá no menu arquivo -> configurar página, clique na aba Margens. No espaço Cabeçalho e Rodapé, defina todas opções para em branco.</li>
<li>Para o Internet Explorer, nas opções de impressão, clique em configurar página. No espaço Cabeçalho e Rodapé, remova as definições e deixe os valores em branco.</li>
</ul>
</div>
	<h:form>
	<a4j:keepAlive beanName="certificadoBancaPos"></a4j:keepAlive>
	
		<h2 class="tituloPagina"> <ufrn:subSistema/> > Emitir Certificado de Participação em Banca &gt; Selecionar
		Banca</h2>

		<c:set value="#{certificadoBancaPos.discente}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>

		
		<center>
			<div class="infoAltRem">			
			    <h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Banca <br/>				
			</div>
		</center>
		
		
		<table class="listagem">
		
			<caption>Bancas cadastradas para o discente</caption>
			<thead>
				<tr>
					<td style="text-align: center;">Data</td>
					<td>Tipo de Banca</td>
					<td>Título</td>
					<td></td>			
				</tr>
			</thead>	
			
			<c:forEach var="b" items="#{ certificadoBancaPos.bancas }"
				varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td width="90" align="center"><fmt:formatDate
						value="${ b.data }" pattern="dd/MM/yyyy" /></td>
					<td>${ b.tipoDescricao }</td>
					<td>${ b.titulo }</td>
					<td align="right">
						<h:commandLink  action="#{certificadoBancaPos.selecionaBanca}"	title="Selecionar Banca" id="botaoSelecionaBanca">
							<h:graphicImage value="/img/seta.gif" />
							<f:param name="id" value="#{b.id}" id="fparamSelecionaBanca"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;">
						<h:commandButton action="#{certificadoBancaPos.iniciar}" value="<< Selecionar Outro Discente" id="buscar"/>
						<h:commandButton action="#{certificadoBancaPos.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" id="Btcancelar"/>
					</td>
				</tr>
			</tfoot>			
		</table>

	</h:form>
</f:view>

<script type="text/javascript">
if ($.browser.mozilla()) {
	$('detalhesConfig').value = "No menu arquivo -> configurar página, clique na aba Margens. No espaço Cabeçalho e Rodapé, defina todas opções para em branco.";
}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>