<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Relatório das Fichas de Avaliação de Apresentação de Resumo do CIC</h2>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Dados para o Relatório</caption>
			<tr>
				<th>Congresso:</th>
				<td>
					<h:selectOneMenu id="congresso" value="#{avaliacaoApresentacaoResumoBean.congresso.id}">
						<f:selectItems value="#{congressoIniciacaoCientifica.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Centro/Unidade:</th>
				<td>
					<h:selectOneMenu id="centro" value="#{avaliacaoApresentacaoResumoBean.unidade.id}">
						<f:selectItem itemLabel="-- TODOS --" itemValue="-1"/>
						<f:selectItems value="#{siglaUnidadePesquisaMBean.unidadesCombo}"/>
						<f:selectItem itemLabel="UFRN" itemValue="605"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Avaliador:</th>
				<td>
					<h:inputHidden id="idServidor" value="#{avaliacaoApresentacaoResumoBean.avaliador.docente.id}"/>
					<h:inputText id="nomeServidor" value="#{avaliacaoApresentacaoResumoBean.avaliador.docente.pessoa.nome}" size="70" onkeyup="CAPS(this);" />
					<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDocente" style="display:none; "> 
					<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
					</span>
				</td>
			</tr>
			<tfoot>
				<tr>
				<td colspan="2">
					<h:commandButton value="Emitir Relatório" action="#{avaliacaoApresentacaoResumoBean.relatorioFichasAvaliacao}" /> 
					<h:commandButton value="Cancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" onclick="#{confirm}" />
				</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
