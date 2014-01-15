<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Edital de Monitoria</h2>
	<a4j:keepAlive beanName="editalMonitoria" />
	<a4j:keepAlive beanName="editalMBean" />
	<h:form prependId="false">
		<h:inputHidden value="#{editalMonitoria.confirmButton}" />
		<h:inputHidden value="#{editalMonitoria.obj.id}" />
		<h:inputHidden value="#{editalMonitoria.obj.edital.idArquivo}" />

		<table class="formulario" width="100%" >
			<caption class="listagem">Cadastrar Edital</caption>
			
			<tr>
				<th width="20%" class="required"> Tipo de Edital: </th>
				<td>
					<h:selectOneMenu id="tipoEdital" value="#{editalMonitoria.obj.tipo}" 
							valueChangeListener="#{editalMonitoria.changeTipoEdital}" onchange="submit()" rendered="#{editalMonitoria.obj.id == 0}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="Z"/>
						<f:selectItem itemLabel="PROJETO INTERNO DE MONITORIA" itemValue="M" />
						<f:selectItem itemLabel="PROJETO EXTERNO DE MONITORIA "	itemValue="X" />
						<f:selectItem itemLabel="PROJETO DE MELHORIA DA QUALIDADE DO ENSINO (PAMQEG)" itemValue="I" />
						<f:selectItem itemLabel="MONITORIA E/OU PAMQEG" itemValue="O" />
					</h:selectOneMenu>
					<b><h:outputText id="txtTipoEdital" value="#{editalMonitoria.obj.edital.tipoString}" rendered="#{editalMonitoria.obj.id > 0}"/></b>
					<ufrn:help>
						<p> Tipo do edital do projeto, podendo ser:<br/> MONITORIA, não possui recursos financeiros.<br/>
							PAMQEG, possui recursos financeiros.<br/>
							MONITORIA E/OU PAMQEG: Pode ser qualquer uma das opções acima, e o professor pode decidir enviar um
							projeto de monitoria, PAMQEG ou os dois.
						</p>
					</ufrn:help>
				</td>
			</tr>
			
			<tr id="periodoRelParcial">
				<th class="required">Proibir Docentes c/ R. Parciais Pendentes entre:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.anoProjetoRelatorioParcialIncio}" required="true"
						id="anoInicio" label="Ano Inicial de R. Parciais Pendentes" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>até
					<h:inputText value="#{editalMonitoria.obj.anoProjetoRelatorioParcialFim}" required="true"
						id="anoFim" label="Ano Final de R. Parciais Pendentes" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>					
					<ufrn:help img="/img/ajuda.gif">Período (ano de início e fim) onde os docentes participantes da equipe de projeto não deverão ter relatórios parciais reprovados ou não enviados.</ufrn:help>
				</td>
			</tr>
			
			<tr id="periodoRelFinal">
				<th class="required" >Proibir Docentes c/ R. Finais Pendentes entre:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.anoProjetoRelatorioFinalIncio}" required="true"
						id="anoInicioRelInicial" label="Ano Inicial R. Finais Pendentes" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>
					até 
					<h:inputText value="#{editalMonitoria.obj.anoProjetoRelatorioFinalFim}" required="true" 
						id="anoFimRelFinal" label="Ano Final R. Finais Pendentes" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/>					
						<ufrn:help img="/img/ajuda.gif">Período (ano de início e fim) onde os docentes participantes da equipe de projeto não deverão ter relatórios finais reprovados ou não enviados.</ufrn:help>
				</td>
			</tr>			

			<tr>
				<th width="40%" class="required">Descrição:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.descricao}" readonly="#{editalMonitoria.readOnly}" size="80" id="descricao"/>
				</td>
			</tr>

			<tr>
				<th class="required">Número do Edital:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.numeroEdital}" size="20" readonly="#{editalMonitoria.readOnly}" maxlength="20" id="numero_edital"/>
				</td>
			</tr>

			<tr>
				<th class="required">Ano-Semestre:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.ano}" readonly="#{editalMonitoria.readOnly}"
						size="4" maxlength="4" onkeyup="return formatarInteiro(this)"/>-
					<h:inputText value="#{editalMonitoria.obj.semestre}" readonly="#{editalMonitoria.readOnly}" 
						size="1" maxlength="1" onkeyup="return formatarInteiro(this)"/>
				</td>
			</tr>

			<tr>
				<th class="required">Data de Publicação:</th>
				<td>
					<t:inputCalendar  id="data_publicacao" value="#{editalMonitoria.obj.dataPublicacao}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th class="required">Iniciar Recebimento de Projetos em:</th>
				<td>
					<t:inputCalendar  id="iniciar_recebimento_em" value="#{editalMonitoria.obj.inicioSubmissao}"  
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th class="required">Receber Projetos até:</th>
				<td>
					<t:inputCalendar  id="receber_projetos_ate" value="#{editalMonitoria.obj.fimSubmissao}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			
			
			<tr>
				<th class="required">Início da Realização:</th>
				<td>
					<t:inputCalendar  id="inicio_realizacao" value="#{editalMonitoria.obj.edital.inicioRealizacao}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr>
				<th class="required">Fim da Realização:</th>
				<td>
					<t:inputCalendar  id="fim_realizacao" value="#{editalMonitoria.obj.edital.fimRealizacao}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			
			
			<tr id="autorizar_ate">
				<th class="required">Autorização dos Departamentos até:</th>
				<td>
					<t:inputCalendar  id="autorizar_projetos_ate" value="#{editalMonitoria.obj.dataFimAutorizacaoDepartamento}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>
			
			<tr id="inicio_selecao">
				<th class="required">Início da Seleção de Monitores:</th>
				<td>
					<t:inputCalendar  id="data_inicio_selecao" value="#{editalMonitoria.obj.inicioSelecaoMonitor}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr id="fim_selecao">
				<th class="required">Fim da Seleção de Monitores:</th>
				<td>
					<t:inputCalendar  id="data_fim_selecao_projetos" value="#{editalMonitoria.obj.fimSelecaoMonitor}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr id="fim_reconsideracao_formal">
				<th class="required">Fim da Reanálise dos Req. Formais:</th>
				<td>
					<t:inputCalendar  id="data_fim_reconsideracao" value="#{editalMonitoria.obj.dataFimReconsideracaoReqFormais}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr id="fim_reconsideracao_avaliacao">
				<th class="required">Início da Reconsideração da Avaliação:</th>
				<td>
					<t:inputCalendar  id="data_inicio_reconsideracao_avaliacao" value="#{editalMonitoria.obj.edital.dataInicioReconsideracao}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr id="fim_reconsideracao_avaliacao">
				<th class="required">Fim da Reconsideração da Avaliação:</th>
				<td>
					<t:inputCalendar  id="data_fim_reconsideracao_avaliacao" value="#{editalMonitoria.obj.edital.dataFimReconsideracao}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr id="resultado_final">
				<th class="required">Data do Resultado Final do Edital:</th>
				<td>
					<t:inputCalendar  id="data_resultado_final" value="#{editalMonitoria.obj.dataResultadoFinalEdital}" 
						renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
						size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				</td>
			</tr>

			<tr id="nota_minima_aprovacao_selecao_monitoria">
				<th class="required">Nota Mínima para Aprovação na Seleção de Monitoria:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.notaMinimaAprovacaoSelecaoMonitora}" label="Nota Mínima para Aprovação na Seleção de Monitoria" required="true" readonly="#{editalMonitoria.readOnly}"
						maxlength="4" size="4" onkeyup="return(formataValor(this, event, 1))">
						<f:converter converterId="convertNota"/>
					</h:inputText>
				</td>
			</tr>

			<tr id="numero_bolsas">
				<th class="required">Número de Bolsas:</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.numeroBolsas}" label="Número de Bolsas" required="true" size="3" readonly="#{editalMonitoria.readOnly}" 
						maxlength="3" id="total_bolsas" onkeyup="return formatarInteiro(this)"/>
				</td>
			</tr>

			<tr>
				<th class="required">Valor do Financiamento (R$):</th>
				<td>
					<h:inputText value="#{editalMonitoria.obj.valorFinanciamento}" size="12" readonly="#{editalMonitoria.readOnly}" 
						 label="Valor do Financiamento (R$)" required="true"
						 maxlength="10" id="valor_financiamento" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>
			
			<tr>
			    <th class="required">Relação Discente/Orientador:</th>
			    <td>
			        <h:inputText id="relacaoDiscenteOrientador" value="#{editalMonitoria.obj.relacaoDiscenteOrientador}"
			        	label="Relação Discente/Orientador" required="true"
			        	size="4" maxlength="3" readonly="#{editalMonitoria.readOnly}" onkeyup="formatarInteiro(this)"/>
		        	<ufrn:help img="/img/ajuda.gif">Determina a quantidade máxima de discentes que cada docente pode orientar.</ufrn:help>
			    </td>
			</tr>
			
			<tr id="fator_carga_horaria">
				<th class="required">Fator de Carga Horária: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.fatorCargaHoraria }" size="2" readonly="#{ editalMonitoria.readOnly }"
						label="Fator de Carga Horária" required="true"
						maxlength="2" id="Fator_carga_horaria" onkeyup="formatarInteiro(this)" />
					<ufrn:help img="/img/ajuda.gif">O Fator de Carga Horária é utlizado na impressão de Certificados/Declarações de Monitoria. Por exemplo, se no edital o fator for 12, a carga horária de um discente que passou 4 semanas no projeto será de 12h X 4 = 48h</ufrn:help>
				</td>
			</tr>
			

			<tr id="peso_media_analise">
				<th>Peso da Média de Análise: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.pesoMediaAnalise }" size="4" readonly="#{ editalMonitoria.readOnly }"
					 	label="Pesa da Média de Análise" required="true"
						maxlength="3" id="Peso_media_analise" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>

			<tr id="peso_numero_doc">
				<th>Peso do Número de Professores: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.pesoNumProfessores }" size="4" readonly="#{ editalMonitoria.readOnly }"
						label="Peso do Número de Professores" required="true" 
						maxlength="3" id="Peso_numero_doc" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>

			<tr id="peso_numero_componentes">
				<th>Peso do Número de Componentes Curriculares: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.pesoCompCurriculares }" size="4" readonly="#{ editalMonitoria.readOnly }"
						label="Peso do Número de Componentes Curriculares" required="true" 
						maxlength="3" id="Peso_numero_componentes" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>

			<tr id="peso_numero_dpto">
				<th>Peso do Número de Departamentos: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.pesoNumDepartamentos }" size="2" readonly="#{ editalMonitoria.readOnly }"
						label="Peso do Número de Departamentos" required="true" 
						maxlength="3" id="Peso_numero_dpto" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>

			<tr id="peso_rt">
				<th>Peso do RT: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.pesoRT }" size="2" readonly="#{ editalMonitoria.readOnly }" maxlength="3"
						label="Peso do RT" required="true" 
						id="Peso_rt" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>
			
			<tr id="media_aprovacao">
				<th>Média para Aprovação: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.mediaAprovacaoProjeto }" size="4" readonly="#{ editalMonitoria.readOnly }"
						label="Média para Aprovação" required="true" 
						maxlength="4" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>

			<tr id="indice_avaliacao_discrepante">
				<th>Índice de Avaliação Discrepante: </th>
				<td>
					<h:inputText value="#{ editalMonitoria.obj.indiceAvaliacaoDiscrepante }" size="4" readonly="#{editalMonitoria.readOnly}" 
						label="Indíce de Avaliação Discrepante" required="true"
						maxlength="4" id="Indice_avaliacao_discrepante" onkeydown="return(formataValor(this, event, 2));"/>
				</td>
			</tr>
			
			
			
			<tr>
			    <td class="subFormulario" colspan="2">Restrições de coordenação</td>
			</tr>
			
			<tr>
			    <th class="required">Máximo de Coordenações Ativas por Docente neste Edital:</th>
			    <td>
			        <h:inputText id="maxCoordenacoesAtivas"  value="#{editalMonitoria.obj.edital.restricaoCoordenacao.maxCoordenacoesAtivas}"
			        size="12"  maxlength="10" onkeyup="return formatarInteiro(this)" readonly="#{editalMBean.readOnly}"/>
			    </td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{editalMonitoria.confirmButton}"
						action="#{editalMonitoria.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{editalMonitoria.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>

	<script type="text/javascript">
	
		function tipoEdital(obj, obj1) {
				
			if (obj == 'M' || obj1 == 'M' || obj == 'B' || obj1 == 'B'){
				$("periodoRelParcial").style.display = "";
				$("periodoRelFinal").style.display = "";
				$("inicio_selecao").style.display = "";
				$("fim_selecao").style.display = "";
				$("numero_bolsas").style.display = "";
				$("peso_media_analise").style.display = "";
				$("peso_numero_doc").style.display = "";
				$("peso_numero_componentes").style.display = "";
				$("peso_numero_dpto").style.display = "";
				$("peso_rt").style.display = "";
				$("fator_carga_horaria").style.display = "";

				
			}else if(obj == 'I' || obj1 == 'I'){
				$("periodoRelParcial").style.display = "none";
				$("periodoRelFinal").style.display = "none";
				$("inicio_selecao").style.display = "none";
				$("fim_selecao").style.display = "none";
				$("numero_bolsas").style.display = "none";
				$("peso_media_analise").style.display = "none";
				$("peso_numero_doc").style.display = "none";
				$("peso_numero_componentes").style.display = "none";
				$("peso_numero_dpto").style.display = "none";
				$("peso_rt").style.display = "none";
				$("fator_carga_horaria").style.display = "none";
			}			
		}

		function comma2Point(texto){
			texto.value = parseFloat(texto.value.replace(',','.'));
			return texto;
		}

		tipoEdital("${editalMonitoria.obj.tipo}", "${editalMonitoria.obj.tipo}");
	</script>
	
	<script type="text/javascript" src="/sigaa/javascript/consolidacao/consolidacao_visualizacao.js"></script>
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
