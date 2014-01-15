<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form id="form">

<h2><ufrn:subSistema/> &gt; Emitir Declaração de Participação em Banca</h2>
<a4j:keepAlive beanName="certificadoBancaPos"></a4j:keepAlive>
<div class="descricaoOperacao">Para uma
melhor impressão da declaração, configure seu navegador para não imprimir o cabeçalho e rodapé.
<ul>
<li>Para o Firefox, vá no menu arquivo -> configurar página, clique na aba Margens. No espaço Cabeçalho e Rodapé, defina todas opções para em branco.</li>
<li>Para o Internet Explorer, nas opções de impressão, clique em configurar página. No espaço Cabeçalho e Rodapé, remova as definições e deixe os valores em branco.</li>
</ul>
</div>

<br/>
<div class="infoAltRem" style="width: 80%">
	<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Emitir Declaração de Participação em Banca
</div>

<table class="listagem" style="width: 80%">
	<caption>Dados da Banca</caption>
	<tr>
		<td colspan="3" class="subFormulario">Dados do Discente</td>
	</tr>
	<tr><td style="text-align: right; font-weight: bold">Discente: </td><td colspan="2">${ certificadoBancaPos.bancaSelecionada.dadosDefesa.discente.nome }</td></tr>
	<tr><td style="text-align: right; font-weight: bold">Tipo: </td><td colspan="2">${ certificadoBancaPos.bancaSelecionada.tipoDescricao }</td></tr>
	<tr><td style="text-align: right; font-weight: bold">Data: </td><td colspan="2"><fmt:formatDate value="${ certificadoBancaPos.bancaSelecionada.data }" pattern="dd/MM/yyyy"/></td></tr>
	<tr><td style="text-align: right; font-weight: bold">Hora: </td><td colspan="2"><fmt:formatDate value="${ certificadoBancaPos.bancaSelecionada.hora }" pattern="HH:mm"/></td></tr>
	<tr><td style="text-align: right; font-weight: bold">Título: </td><td colspan="2">${ certificadoBancaPos.bancaSelecionada.dadosDefesa.titulo }</td></tr>
	<tr>
		<td colspan="3" class="subFormulario">Membros da Banca</td>
	</tr>
	<tr class="linhaCinza">
		<td>Tipo de Participação</td>
		<td>Docente</td>
		<td></td>
	</tr>
	<c:forEach var="m" items="#{ certificadoBancaPos.bancaSelecionada.membrosBanca }" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td width="25%">
				${ m.tipoDescricao }
			</td>
			<td>
				${ m.nome } (${ m.instituicao.sigla })
			</td>
			<td align="right">
				<h:commandLink action="#{certificadoBancaPos.gerarCertificado}" title="Emitir Declaração de Participação em Banca" id="linkGerarCertificado">
					<h:graphicImage value="/img/seta.gif"/>
					<f:param name="id" value="#{m.id}" id="idMembroDaBanca"/>
				</h:commandLink>
			</td>
		</tr>
	</c:forEach>
	<tfoot>
		<tr>
			<td colspan="3" style="text-align: center;">
				<h:commandButton action="#{certificadoBancaPos.selecionaDiscente}" rendered="#{fn:length(certificadoBancaPos.bancas) > 1}" value="<< Voltar" id="btVoltar"/>
				<h:commandButton action="#{certificadoBancaPos.iniciar}" value="<< Selecionar Outro Discente" id="buscar"/>
				<h:commandButton action="#{certificadoBancaPos.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" id="Btcancelar"/>
			</td>
		</tr>
	</tfoot>
</table>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>