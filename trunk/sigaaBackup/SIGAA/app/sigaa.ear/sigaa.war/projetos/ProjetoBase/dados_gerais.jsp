<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
<!--

	function semFinanciamento(obj){

		if (obj.checked == true){			
			$('lineAuto').style.display = "none";
			$('lineInterno').style.display = "none";
			$('lineExterno').style.display = "none";
			$('lineFinanciador').style.display = "none";			
		}else{
			$('lineAuto').style.display = "";
			$('lineInterno').style.display = "";
			$('lineExterno').style.display = "";
			$('lineFinanciador').style.display = "";
		}

	}

	function habilitarDadosFinExt(obj, id){
		var area = $(id);
		if (obj.checked) area.style.display = "block"; 
		else area.style.display = "none"; 
		
	}

	function limitText(limitField, limitCount, limitNum) {
	    if (limitField.value.length > limitNum) {
	        limitField.value = limitField.value.substring(0, limitNum);
	    } else {
	        $(limitCount).value = limitNum - limitField.value.length;
	    }
	}
	
//-->
</script>

<f:view>

	<h:messages showDetail="true" showSummary="true" />
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Proposta de Ação Acadêmica Integrada (PROJETO BASE)</h2>

	<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td>Nesta tela devem ser informados os dados gerais de um
			Projeto de Ação Acadêmica Integrada.</td>
			<td><%@include file="passos_projeto.jsp"%>
			</td>
		</tr>
	</table>
	</div>

	<h:form id="frmDadosGerais">

		<table class="formulario" width="100%">
			<caption>Informe os dados Gerais do	Projeto</caption>

			<tr>
				<th class="required" width="25%">Título:</th>
				<td><h:inputTextarea id="titulo" label="Título"
					value="#{projetoBase.obj.titulo}" cols="2"
					readonly="#{projetoBase.readOnly}" style="width: 95%">
					</h:inputTextarea></td>
			</tr>

            <c:if test="${ projetoBase.obj.interno && projetoBase.obj.financiamentoInterno }">
			<tr>
				<th class="required"> Edital: </th>
				<td>
					<a4j:region rendered="#{not empty editalMBean.editaisCombo}"> 
						<h:selectOneMenu id="edital" value="#{projetoBase.obj.edital.id}" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{editalMBean.editaisCombo}"/>
							<a4j:support event="onchange" reRender="anoProjeto, periodoProjeto" action="#{projetoBase.changeEdital}"/>
						</h:selectOneMenu>
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif">Carregando dados do edital</h:graphicImage>
							</f:facet>
						</a4j:status>
					</a4j:region>
					<h:outputText id="edital_selecionado" value="#{projetoBase.obj.edital.descricao}" rendered="#{empty editalMBean.editaisCombo}" />
				</td>
			</tr>
			</c:if>

            <c:if test="${ projetoBase.obj.interno && projetoBase.obj.financiamentoInterno }">
				<tr>
					<th><b>Ano:</b></th>
					<td>
						<h:outputText id="anoProjeto" value="#{projetoBase.obj.ano}" />
					</td>
				</tr>
			</c:if>
			
			<c:if test="${projetoBase.obj.interno == false || (projetoBase.obj.interno && projetoBase.obj.financiamentoInterno == false)}">			
	            <tr>
	                <th class="required">Ano:</th>
	                <td>
	                    <h:inputText id="anoProjeto" value="#{projetoBase.obj.ano}" size="6" onkeyup="formatarInteiro(this)"/>
	                </td>
	            </tr>
            </c:if>
            
            <tr>
                <th class="required">Período:</th>
                <td>                
		            <t:inputCalendar id="dataInicio" value="#{projetoBase.obj.dataInicio}" 
		                renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
		                size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
		                <f:converter converterId="convertData" />
		            </t:inputCalendar>
		            a <t:inputCalendar  id="dataFim" value="#{projetoBase.obj.dataFim}" 
		                renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
		                size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
		                <f:converter converterId="convertData" />
		            </t:inputCalendar>              
                </td>
            </tr>
            
            <tr>
                 <th class="required">Número de discentes envolvidos:</th>
                 <td>
                     <h:inputText id="totalDiscentesEnvolvidos" maxlength="7" label="Número de discentes envolvidos" value="#{projetoBase.obj.totalDiscentesEnvovidos}" size="5" onkeyup="formatarInteiro(this)"/>
                 </td>
            </tr>

            <tr>
                 <th class="required">Número de Bolsas Solicitadas:</th>
                 <td>
                     <h:inputText id="bolsasSolicitadas" maxlength="7" label="Número de Bolsas Solicitadas" value="#{projetoBase.obj.bolsasSolicitadas}" size="5" onkeyup="formatarInteiro(this)"/>
                 </td>
            </tr>            

			<tr>
				<th class="required">Área do CNPq:</th>
				<td><h:selectOneMenu id="areaCNPQ"
					value="#{projetoBase.obj.areaConhecimentoCnpq.id}"
					readonly="#{projetoBase.readOnly}" style="width: 70%;">
					<f:selectItem itemValue="0"
						itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{area.allGrandesAreasCombo}" />
				</h:selectOneMenu></td>
			</tr>

			<tr>
				<th  class="required">Abrangência:</th>
				<td>
					<h:selectOneMenu id="tipoRegiao"	
						value="#{projetoBase.obj.abrangencia.id}"	
						readonly="#{projetoBase.readOnly}" style="width: 40%;">
						<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
						<f:selectItems value="#{tipoRegiao.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="required">Unidade Proponente:</th>
				<td><h:selectOneMenu id="unidadeProponente"
					value="#{projetoBase.obj.unidade.id}"
					readonly="#{projetoBase.readOnly}" style="width: 85%">
					<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />					
						<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
				    </h:selectOneMenu>
				</td>
			</tr>

            <c:if test="${projetoBase.obj.interno == false}"> 
            <tr>
                <th class="required">Renovação:</th>
                <td>
                    <table>
                        <tr>
                            <td valign="top">
                                <h:selectOneRadio value="#{projetoBase.obj.renovacao}" id="renovacao" layout="lineDirection">
                                    <f:selectItem itemLabel="SIM" itemValue="true"/>
                                    <f:selectItem itemLabel="NÃO" itemValue="false"/>           
                                </h:selectOneRadio>
                            </td>
                            <td>
                                <ufrn:help img="/img/ajuda.gif">Marque esta opção em caso de renovação de projeto anteriormente aprovado.</ufrn:help>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            </c:if>

			<tr>
				<td colspan="2" class="subFormulario">Natureza do Financiamento:<h:graphicImage url="/img/required.gif"	style="vertical-align: top;" /></td>
			</tr>

			<c:if test="${not projetoBase.obj.financiamentoInterno}">
				<tr>
					<th>Sem Financiamento:</th>
					<td><h:selectBooleanCheckbox value="#{projetoBase.obj.semFinanciamento}" id="chk_semFinanciamento" onclick="semFinanciamento(this);"/></td>
				</tr>
			</c:if>	

			<tr id="lineAuto" style="display: ${projetoBase.obj.semFinanciamento ? 'none':'' }">
				<th>Auto-financiado:</th>
				<td>
					<h:selectBooleanCheckbox value="#{projetoBase.obj.autoFinanciado}" id="chk_autoFinanciado"/>
					<small><i>Marque esta opção se o seu projeto for financiado com recursos próprios.</i></small>					
				</td>
			</tr>

            <c:if test="${projetoBase.obj.interno && projetoBase.obj.financiamentoInterno}">
				<tr id="lineInterno" style="display: ${projetoBase.obj.semFinanciamento ? 'none':'' }">
					<th valign="top">Financiamento Interno: </th>
					<td>
						<h:selectBooleanCheckbox value="#{projetoBase.obj.financiamentoInterno}" id="chk_financiamentoInterno" disabled="true" />
						<small><i>Marque esta opção se seu projeto for financiado com recursos da universidade.</i></small>
					</td>
				</tr>
            </c:if>

			<tr id="lineExterno" style="display: ${projetoBase.obj.semFinanciamento ? 'none':'' }">
				<th>Financiamento Externo:</th>
				<td><h:selectBooleanCheckbox value="#{projetoBase.obj.financiamentoExterno}" id="chk_financiamentoExterno" onclick="javascript:habilitarDadosFinExt(this,'finExt');"/></td>
			</tr>
			
			<tr id="lineFinanciador" style="display: ${projetoBase.obj.semFinanciamento ? 'none':'' }">
				<th></th>
				<td>
					<table style="display: ${projetoBase.obj.financiamentoExterno ? '':'none'}" id="finExt">
						<tr>
							<th class="required">Financiador:</th>
							<td>
									<h:selectOneMenu id="classeFinanciamento" value="#{projetoBase.obj.classificacaoFinanciadora.id}"	
									readonly="#{atividadeExtensao.readOnly}" onchange="javascript:$('formDadosGerais:chk_financiamentoExterno').checked = true;">
										<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --"/>
										<f:selectItems value="#{classificacaoFinanciadora.allCombo}"/>
									</h:selectOneMenu>&nbsp;
									<h:inputText  id="descricaoFinanciadorExterno"  value="#{projetoBase.obj.descricaoFinanciadorExterno}"	
									 maxlength="255" readonly="#{projetoBase.readOnly}" size="25" onchange="javascript:$('formDadosGerais:chk_financiamentoExterno').checked = true;"/>
									 <ufrn:help img="/img/ajuda.gif">Detalhes da entidade externa financiadora do projeto (Ex.: Nº do Edital e/ou Nome da entidade).</ufrn:help>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<th></th>
				<td></td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Detalhes do projeto</td>
			</tr>
			
            <tr>
                <td colspan="2">
                    <div class="descricaoOperacao">			
            			Explicite nos detalhes do projeto as inter-relações entre as ações acadêmicas envolvidas neste projeto: 
            			<b>
            			 <h:outputText value="Ensino e Pesquisa" rendered="#{(projetoBase.obj.ensino && projetoBase.obj.pesquisa && !projetoBase.obj.extensao)}"/> 
            			 <h:outputText value="Ensino e Extensão" rendered="#{(projetoBase.obj.ensino && projetoBase.obj.extensao && !projetoBase.obj.pesquisa)}"/>
            			 <h:outputText value="Pesquisa e Extensão" rendered="#{(projetoBase.obj.extensao && projetoBase.obj.pesquisa && !projetoBase.obj.ensino)}"/> 
            			 <h:outputText value="Ensino, Pesquisa e Extensão" rendered="#{(projetoBase.obj.extensao && projetoBase.obj.pesquisa && projetoBase.obj.ensino)}"/>
            			</b>.
           			</div>
           		</td>
           </tr>
			
			
			<tr>
				<td colspan="2">
					<div id="tabs-dados-projeto" class="reduzido">
			
						<div id="resumo" class="aba">
							<i>	Resumo do Projeto:</i><span class="required">&nbsp;</span><br/>
							<h:inputTextarea id="resumo" value="#{projetoBase.obj.resumo}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countResumo, 4000);" onkeyup="limitText(this, countResumo, 4000);"/>
							<center>
                                 Você pode digitar <input readonly type="text" id="countResumo" size="3" value="${4000 - fn:length(projetoBase.obj.resumo) < 0 ? 0 : 4000 - fn:length(projetoBase.obj.resumo)}"> caracteres.
                            </center>							
						</div>

						<div id="justificativa" class="aba">
							<i>Introdução e Justificativa para execução do projeto:</i><span class="required">&nbsp;</span><br/>
							<div class="descricaoOperacao">Inclua na justificativa os benefícios esperados no processo ensino-aprendizagem dos alunos de graduação e/ou pós-graduação vinculados ao projeto. 
							Explicite também o retorno para os cursos de graduação e/ou pós-graduação e para os professores da ${ configSistema['siglaInstituicao'] } em geral.</div>
							<h:inputTextarea id="justificativa" value="#{projetoBase.obj.justificativa}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countJustificativa, 4000);" onkeyup="limitText(this, countJustificativa, 4000);"/>
							<center>
							     Você pode digitar <input readonly type="text" id="countJustificativa" size="3" value="${4000 - fn:length(projetoBase.obj.justificativa) < 0 ? 0 : 4000 - fn:length(projetoBase.obj.justificativa)}"> caracteres.
							</center>
						</div>

						<div id="objetivos" class="aba">
							<i>	Objetivos (Geral e específico):</i><span class="required">&nbsp;</span><br/>
							<h:inputTextarea id="objetivos" value="#{projetoBase.obj.objetivos}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countObjetivos, 4000);" onkeyup="limitText(this, countObjetivos, 4000);"/>
							<center>
                                Você pode digitar <input readonly type="text" id="countObjetivos" size="3" value="${4000 - fn:length(projetoBase.obj.objetivos) < 0 ? 0 : 4000 - fn:length(projetoBase.obj.objetivos)}"> caracteres.
                            </center>							
						</div>

						<div id="resultados" class="aba">
							<i>Resultados Esperados:</i><span class="required">&nbsp;</span><br/>
							<h:inputTextarea id="resultados" value="#{projetoBase.obj.resultados}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countResultados, 4000);" onkeyup="limitText(this, countResultados, 4000);"/>
                            <center>
                                Você pode digitar <input readonly type="text" id="countResultados" size="3" value="${4000 - fn:length(projetoBase.obj.resultados) < 0 ? 0 : 4000 - fn:length(projetoBase.obj.resultados)}"> caracteres.
                            </center>                           
						</div>
			
						<div id="metodologia" class="aba">
							<i>Metodologia de Avaliação do Desenvolvimento do Projeto:</i><span class="required">&nbsp;</span><br/>
							<h:inputTextarea id="metodologia" value="#{projetoBase.obj.metodologia}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countMetodologia, 4000);" onkeyup="limitText(this, countMetodologia, 4000);"/>
							<center>
							     Você pode digitar <input readonly type="text" id="countMetodologia" size="3" value="${4000 - fn:length(projetoBase.obj.metodologia) < 0 ? 0 : 4000 - fn:length(projetoBase.obj.metodologia)}"> caracteres.
							</center>
						</div>
			
						<div id="referencias" class="aba">
							<i>Referências:  Ref. Bibliográficas do projeto, etc.</i><span class="required">&nbsp;</span><br/>
							<h:inputTextarea id="referencias" value="#{projetoBase.obj.referencias}" rows="10" style="width:99%" 
							readonly="#{projetoBase.readOnly}" onkeydown="limitText(this, countReferencias, 4000);" onkeyup="limitText(this, countReferencias, 4000);"/>
                            <center>
                                Você pode digitar <input readonly type="text" id="countReferencias" size="3" value="${4000 - fn:length(projetoBase.obj.referencias) < 0 ? 0 : 4000 - fn:length(projetoBase.obj.referencias)}"> caracteres.
                            </center>							
						</div>
			
					</div>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{projetoBase.passoAnterior}" id="btPassoAnteriorDadosGerais"/>
						<h:commandButton value="Cancelar"	action="#{projetoBase.cancelar}" onclick="#{confirm}" id="btCancelar" /> 
						<h:commandButton value="Gravar e Avançar >>" action="#{projetoBase.submeterDadosGerais}" id="btSubmeterDadosGerais" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br />

</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-dados-projeto');
	        tabs.addTab('resumo', "Resumo");
	        tabs.addTab('justificativa', "Introdução/Justificativa");
	        tabs.addTab('objetivos', "Objetivos (Geral e específico)");
	        tabs.addTab('resultados', "Resultados Esperados");
	        tabs.addTab('metodologia', "Metodologia");
	        tabs.addTab('referencias', "Referências");	        
	        tabs.activate('resumo');
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);

</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>