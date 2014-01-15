<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form id="form">

<h2><ufrn:subSistema/> &gt; Emitir Declara��o de Participa��o em Banca</h2>
<a4j:keepAlive beanName="certificadoBancaPos"></a4j:keepAlive>
<div class="descricaoOperacao">Para uma
melhor impress�o da declara��o, configure seu navegador para n�o imprimir o cabe�alho e rodap�.
<ul>
<li>Para o Firefox, v� no menu arquivo -> configurar p�gina, clique na aba Margens. No espa�o Cabe�alho e Rodap�, defina todas op��es para em branco.</li>
<li>Para o Internet Explorer, nas op��es de impress�o, clique em configurar p�gina. No espa�o Cabe�alho e Rodap�, remova as defini��es e deixe os valores em branco.</li>
</ul>
</div>

<br/>
<div class="infoAltRem" style="width: 80%">
	<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Emitir Declara��o de Participa��o em Banca
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
	<tr><td style="text-align: right; font-weight: bold">T�tulo: </td><td colspan="2">${ certificadoBancaPos.bancaSelecionada.dadosDefesa.titulo }</td></tr>
	<tr>
		<td colspan="3" class="subFormulario">Membros da Banca</td>
	</tr>
	<tr class="linhaCinza">
		<td>Tipo de Participa��o</td>
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
				<h:commandLink action="#{certificadoBancaPos.gerarCertificado}" title="Emitir Declara��o de Participa��o em Banca" id="linkGerarCertificado">
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