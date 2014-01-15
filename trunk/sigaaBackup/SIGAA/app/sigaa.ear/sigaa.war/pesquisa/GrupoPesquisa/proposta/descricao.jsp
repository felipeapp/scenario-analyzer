<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
function limitText(limitField, limitCount) {
    if (limitField.value.length > ${propostaGrupoPesquisaMBean.totalCaracteres}) {
        limitField.value = limitField.value.substring(0, ${propostaGrupoPesquisaMBean.totalCaracteres});
    } else {
        $(limitCount).value = ${propostaGrupoPesquisaMBean.totalCaracteres} - limitField.value.length;
    }
}
</script>

<f:view>
<a4j:region rendered="#{ propostaGrupoPesquisaMBean.portalDocente }">
<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>

<h2><ufrn:subSistema /> &gt; Proposta de Cria��o de Grupos de Pesquisa</h2>

	<h:form id="form">
		<table class="formulario" width="90%">
		  <caption>Descri��o Detalhada</caption>
			<tbody>
				<tr>
					<td>
						<div id="abas-descricao">
							<div id="justificativa" class="aba">
								<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
								&nbsp;<i>Justificativa Hist�rica e Te�rica para Forma��o do Grupo (demonstrar a relev�ncia e as perspectivas de contribui��o cient�fica)</i><br /><br />
								<h:inputTextarea id="justificativa" value="#{propostaGrupoPesquisaMBean.obj.justificativa}" style="width: 98%" rows="10" 
								onkeydown="limitText(this, countJust);" onkeyup="limitText(this, countJust);"/>
								<center>
	                                 Voc� pode digitar <input readonly type="text" id="countJust" size="3" value="${propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.justificativa) < 0 ? 0 : 
	                                 propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.justificativa)}"> caracteres.
	                            </center>							
							</div>
		
							<div id="instituicoes_intercambio" class="aba">
								<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
								&nbsp;<i>Institui��es com as quais colabora e descri��o de interc�mbio com pesquisadores locais ou de outras institui��es</i><br /><br />
								<h:inputTextarea id="instituicoes_intercambio" value="#{propostaGrupoPesquisaMBean.obj.instituicoesIntercambio}" style="width: 98%" rows="10"
								onkeydown="limitText(this, countInst);" onkeyup="limitText(this, countInst);"/>
								<center>
	                                 Voc� pode digitar <input readonly type="text" id="countInst" size="3" value="${propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.instituicoesIntercambio) < 0 ? 0 : 
	                                 propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.instituicoesIntercambio)}"> caracteres.
	                            </center>							
							</div>
							
							<div id="infraestrutura" class="aba">
								<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
								&nbsp;<i>Descri��o da Infraestrutura Dispon�vel</i><br /><br />
								<h:inputTextarea id="infraestrutura" value="#{propostaGrupoPesquisaMBean.obj.infraestrutura}" style="width: 98%" rows="10"
								onkeydown="limitText(this, countInfra);" onkeyup="limitText(this, countInfra);"/>
								<center>
	                                 Voc� pode digitar <input readonly type="text" id="countInfra" size="3" value="${propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.infraestrutura) < 0 ? 0 : 
	                                 propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.infraestrutura)}"> caracteres.
	                            </center>							
							</div>
							
							<div id="laboratorios" class="aba">
								<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
								&nbsp;<i>Laborat�rio(s) Vinculado(s) ao Grupo</i><br /><br />
								<h:inputTextarea id="laboratorios" value="#{propostaGrupoPesquisaMBean.obj.laboratorios}" style="width: 98%" rows="10"
								onkeydown="limitText(this, countLab);" onkeyup="limitText(this, countLab);"/>
								<center>
	                                 Voc� pode digitar <input readonly type="text" id="countLab" size="3" value="${propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.laboratorios) < 0 ? 0 : 
	                                 propostaGrupoPesquisaMBean.totalCaracteres - fn:length(propostaGrupoPesquisaMBean.obj.laboratorios)}"> caracteres.
	                            </center>							
							</div>
							
						</div>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td>
						<h:commandButton id="btnVoltar"   value="<< Voltar" action="#{ propostaGrupoPesquisaMBean.telaProjetosVinculados }" />
						<h:commandButton id="btnCancelar" value="Cancelar" onclick="#{ confirm }" action="#{ propostaGrupoPesquisaMBean.cancelar }" />
						<h:commandButton id="btnAvancar"  value="Avan�ar >>" action="#{ propostaGrupoPesquisaMBean.submeterDescricao }" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	<script>
		var Abas = {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('abas-descricao');
		        abas.addTab('justificativa', "Justificativa");
		        abas.addTab('instituicoes_intercambio', "Institui��es");
		        abas.addTab('infraestrutura', "Infraestrutura");
		        abas.addTab('laboratorios', "Laborat�rios");
		        abas.activate('justificativa');
		    }
		};
		YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>