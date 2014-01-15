<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2> <ufrn:subSistema /> > Cadastro de Aluno de Stricto</h2>
	
	<c:if test="${discenteStricto.obj.id > 0}">
		<c:set value="#{discente}" var="discenteMBean" />
		<c:set value="#{discenteStricto.obj}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
	<c:set value="#{discenteMBean}" var="discente" />
	</c:if>
	
	<h:form id="formDiscenteStricto">
	<h:inputHidden value="#{discenteStricto.obj.id}" id="idDiscenteStricto"/>
	<table class="formulario" style="width: 100%">
		<caption>Dados do Discente</caption>
		<tbody>
			<tr>
				<th style="width: 20%;" class="rotulo"> Nome: </th>
				<td>${discenteStricto.obj.nome}</td>
			</tr>
			<c:if test="${discenteStricto.discenteAntigo}">
			<tr>
				<th class="required"> Matrícula: </th>
				<td>
					<h:inputText id="matricula" size="10" maxlength="10" value="#{ discenteStricto.obj.matricula }"  onkeyup="formatarInteiro(this);" rendered="#{discenteStricto.discenteAntigo}" />
				</td>
			</tr>
			<tr>
				<th class="required">Status: </th>
				<td>
					<h:selectOneMenu value="#{discenteStricto.obj.status}" id="status" style="width: 40%;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discente.statusCombo}" id="statusDiscenteCombo"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			</c:if>
		
			<tr>
				<th class="${ discenteStricto.obj.id > 0 && !acesso.ppg || discenteStricto.blockAnoSemestre ?'rotulo':'required'}"><h:outputLabel id="labAnoSemestre" value="Ano-Semestre Inicial:"/></th>
				<td>
					<c:if test="${ discenteStricto.obj.id == 0 || (discenteStricto.obj.id > 0 and acesso.ppg) }">
						<h:inputText id="ano" size="4" onkeyup="return formatarInteiro(this);" maxlength="4" value="#{ discenteStricto.obj.anoIngresso }" readonly="#{discenteStricto.blockAnoSemestre}" disabled="#{discenteStricto.blockAnoSemestre}" /> -
						<h:inputText id="periodo" size="1" onkeyup="return formatarInteiro(this);" maxlength="1" value="#{ discenteStricto.obj.periodoIngresso }"  readonly="#{discenteStricto.blockAnoSemestre}" disabled="#{discenteStricto.blockAnoSemestre}"/>
					</c:if>
					<c:if test="${ discenteStricto.obj.id > 0 && !acesso.ppg }">
						<h:outputText id="ano" value="#{ discenteStricto.obj.anoIngresso }"/> -
						<h:outputText id="periodo" value="#{ discenteStricto.obj.periodoIngresso }"/>
					</c:if>		
				</td>
			</tr>
			<tr>
				<th class="${ acesso.ppg || (discenteStricto.obj.id == 0 && (acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto)  )  ?'required':'rotulo'}">Mês de Entrada: </th>
				<td>
					<c:if test="${ acesso.ppg || (discenteStricto.obj.id == 0 && (acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto)  ) }">
						<h:selectOneMenu id="mesEntrada" value="#{ discenteStricto.obj.mesEntrada }"  style="width: 40%" >
							<f:selectItem itemValue="" itemLabel="-- SELECIONE --"  />
							<f:selectItems value="#{discenteStricto.meses}" id="discenteStrictoMeses"/>
						</h:selectOneMenu>
					</c:if>
					<c:if test="${ !(acesso.ppg || (discenteStricto.obj.id == 0 && (acesso.secretariaPosGraduacao || acesso.coordenadorCursoStricto)  )) }">
					 	<h:outputText id="outputTextmesEntrada" value="#{ discenteStricto.mesEntradaFormatado}"/>
					</c:if>						
				</td>						
			</tr>
			<tr>
				<th><h:outputLabel id="labOrigemDiscente" value="Origem do discente:" styleClass="required"/></th>
				<td>
					<h:selectOneMenu id="origemDiscentePos" value="#{ discenteStricto.obj.origem.id }"  style="width: 40%" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
						<f:selectItems value="#{origemDiscentePosBean.allCombo}" id="itensOrigemDiscentePos"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="${ discenteStricto.obj.id > 0 && !acesso.ppg?'rotulo':'' }"><h:outputLabel id="labTipo" value="Tipo:" styleClass="#{ discenteStricto.obj.id > 0 && !acesso.ppg?'rotulo':'required' }"/></th>
				<td>
				<c:if test="${ discenteStricto.obj.id == 0 || (discenteStricto.obj.id > 0 and acesso.ppg) }">
				<a4j:region>
					<h:selectOneMenu value="#{discenteStricto.obj.tipo}" id="tipo" 
						valueChangeListener="#{discenteStricto.filtraFormaEntrada}"  style="width: 40%" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discente.tipoCombo}" />
						<a4j:support event="onchange" oncomplete="exibirCurriculo(this)" reRender="formaIngresso, labelCurso, divProcessoSeletivo, curso_nivel" />
					</h:selectOneMenu>
					<a4j:status>
		                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</c:if>
				<c:if test="${ discenteStricto.obj.id > 0 && !acesso.ppg }">
				<h:outputText value="#{discenteStricto.obj.tipoString}"/>
				</c:if>
				</td>
			</tr>
			<tr>
				<th><h:outputLabel id="labFormaIngresso" value="Forma de Ingresso:" styleClass="required"/></th>
				<td>
					<h:selectOneMenu value="#{discenteStricto.obj.formaIngresso.id}" id="formaIngresso" style="width: 40%" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discenteStricto.formasIngressoCombo}" id="itensFormaIngressoCombo"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th>
					<h:outputLabel id="divProcessoSeletivo" value="Processo Seletivo:" styleClass="#{discenteStricto.processoSeletivoObrigatorio ? 'required' : ''}" />
				</th>
				<td>
					<h:selectOneMenu value="#{discenteStricto.obj.processoSeletivo.id}" id="processoSeletivo" style="width: 70%" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discenteStricto.processosSeletivosCombo}" id="itensProcessosSeletivos"/>
					</h:selectOneMenu>
				</td>
			</tr>			

			<c:if test="${acesso.ppg}">
			<tr>
				<th class="required">Programa:</th>
				<td>
				<a4j:region>
					<h:selectOneMenu value="#{discenteStricto.obj.gestoraAcademica.id}" id="Programa"
						valueChangeListener="#{discenteStricto.carregarCursos }" style="width: 95%" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{unidade.allProgramaPosCombo}" id="itensProgramasCombo"/>
						<a4j:support event="onchange" reRender="curso,linhaPesquisa,orientador,coorientador" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>
			</c:if>

			<tr>
				<th>
					<h:outputLabel id="curso_nivel" value="#{discenteStricto.obj.regular ? 'Curso:' : 'Nível:'}" styleClass="required" />
				</th> 
				<td>
					<a4j:region>
						<h:selectOneMenu id="curso" value="#{discenteStricto.obj.curso.id}" 
						valueChangeListener="#{discenteStricto.carregarAreasLinhas}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{discenteStricto.possiveisCursos}" id="itensPossiveisCursos"/>
							<a4j:support event="onchange" reRender="curriculo, areaConcentracao" />
						</h:selectOneMenu>
						<a4j:status>
				                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
			            </a4j:status>
					</a4j:region>
				</td>
			</tr>
			
			<!-- SE FOR ALTERACAO O USUARIO PODE SELECIONAR O CURRICULO DO DISCENTE -->
			<tr id="trCurriculo">
				<th class="required">Currículo:</th>
				<td>
					<h:selectOneMenu id="curriculo" value="#{discenteStricto.obj.curriculo.id}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteStricto.possiveisCurriculos}" id="itensPossiveisCurriculos"/>
					</h:selectOneMenu>
				</td>
			</tr>				

			<tr>
				<th><h:outputLabel id="labAreaConcentracao" value="Área de Concentração:" styleClass="required"/></th>
				<td>
				<a4j:region>
					<h:selectOneMenu id="areaConcentracao" value="#{discenteStricto.obj.area.id}"
						valueChangeListener="#{discenteStricto.carregarLinhas}"  style="width: 60%" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteStricto.possiveisAreas}" id="itensPossiveisAreas"/>
						<a4j:support event="onchange" reRender="linhaPesquisa" />
					</h:selectOneMenu>
					<a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
				</td>
			</tr>

			<tr>
				<th>Linha de Pesquisa:</th>
				<td>
					<h:selectOneMenu id="linhaPesquisa" value="#{discenteStricto.obj.linha.id}" style="width: 60%" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{discenteStricto.possiveisLinhas}" id="itensPossiveisLinasPesquisa"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<c:if test="${discenteStricto.obj.id == 0}">
				<tr>
					<th>Orientador:	</th>
					<td>
						<h:selectOneMenu id="orientador" value="#{discenteStricto.orientador.id}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{equipePrograma.docentesProgramaCombo}" id="itensEquipeOrientador"/>
							<a4j:support event="onchange" reRender="labInicioOrientacao" actionListener="#{discenteStricto.carregarOrientador}" ajaxSingle="true" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:outputLabel id="labInicioOrientacao" value="Início da Orientação:" styleClass="#{discenteStricto.orientador.id > 1 ? 'required' : ''}" />
					</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" 
						onkeypress="return formataData(this,event);" value="#{discenteStricto.inicioOrientador}" 
						id="inicioOrientacao" popupDateFormat="dd/MM/yyyy"> 
						<f:converter converterId="convertData"/> 
						</t:inputCalendar> 	
					</td>
				</tr>
				<tr>
					<th>Co-Orientador:</th>
					<td>
						<h:selectOneMenu id="coorientador" value="#{discenteStricto.coOrientador.id}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{equipePrograma.docentesProgramaCombo}" id="itensEquipeCoorientador" />
							<a4j:support event="onchange" reRender="labInicioCoOrientacao" actionListener="#{discenteStricto.carregarCoOrientador}" ajaxSingle="true" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>
						<h:outputLabel id="labInicioCoOrientacao" value="Início da Co-Orientação:" styleClass="#{discenteStricto.coOrientador.id > 1 ? 'required' : ''}" />
					</th>
					<td>
						<t:inputCalendar renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" 
						onkeypress="return formataData(this,event);" value="#{discenteStricto.inicioCoOrientador}" 
						id="inicioCoOrientacao" popupDateFormat="dd/MM/yyyy"> 
						<f:converter converterId="convertData"/> 
						</t:inputCalendar> 	
					</td>
				</tr>
			</c:if>
			
			<c:if test="${discenteStricto.obj.id > 0}">
				<c:if test="${discenteStricto.obj.orientacao.id>0}">
					<tr>
						<th>Orientador:</th>
						<td>${discenteStricto.obj.orientacao.descricaoOrientador}</td>
					</tr>
					<tr>
						<th>Data Início da Orientação:</th>
						<td><h:outputText value="#{discenteStricto.obj.orientacao.inicio}"/></td>
					</tr>
				</c:if>
				
				<c:if test="${discenteStricto.obj.coOrientacao.id>0}">
					<tr>
						<th>CoOrientador:</th>
						<td>${discenteStricto.obj.coOrientacao.descricaoOrientador}</td>
					</tr>
					<tr>
						<th>Início da Co-Orientação:</th>
						<td><h:outputText value="#{discenteStricto.obj.coOrientacao.inicio}"/></td>
					</tr>
				</c:if>
			</c:if>	
			
			<tr>
				<th>Co-Orientadores Externos:</th>
				<td>
 					<h:inputTextarea  cols="60" rows="1" id="coorientadores" value="#{discenteStricto.obj.coOrientadoresExternos}" />
				</td>
			</tr>			

			<%-- Será Inserido novamente no término da tarefa 28682 
			<c:if test="${acesso.ppg or discenteStricto.obj.emAssociacao}">
				<tr>
					<th>Pertence a outra Instituição de Ensino:</th>
					<td>
						<h:selectBooleanCheckbox id="checkAlunoOutraInstituicao" disabled="#{!acesso.ppg}"
							value="#{discenteStricto.alunoOutraInstituicao}" styleClass="noborder" onclick="escolherInstituicao(this)" /> 
						<ufrn:help img="/img/ajuda.gif">Marque esta opção caso este Aluno pertença a um programa em rede de ensino, sendo este proveniente de outra Instituição Federal de Ensino.</ufrn:help>
					</td>
				</tr>
				<tr id="instituicao">
					<th class="${acesso.ppg ? 'required' : ''}">Outra Instituição de Ensino:</th>
					<td>
					<c:if test="${acesso.ppg}">	
						<h:selectOneMenu value="#{discenteStricto.obj.instituicaoEnsinoRede.id}" id="selectInstituicaoEnsino" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{instituicoesEnsino.allCombo}"/>
						</h:selectOneMenu>
					</c:if>	
					<c:if test="${!acesso.ppg}">
						${discenteStricto.obj.instituicaoEnsinoRede.nome}
					</c:if>	
					</td>
				</tr>
			</c:if>
			 --%>	
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:inputHidden value="#{discenteStricto.blockAnoSemestre}"></h:inputHidden>
				<h:commandButton value="Confirmar" id="Cadastrar" action="#{ discenteStricto.cadastrar }"/>
				<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" immediate="true" rendered="#{discenteStricto.obj.id==0}"
					 action="#{discenteStricto.telaDadosPessoais}"/>
				<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ discenteStricto.cancelar }" onclick="#{confirm}"/>
			</td></tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>


<script>
function exibirCurriculo(sel){
	var val = sel.options[sel.selectedIndex].value;
		if (val != "1") {
			$('trCurriculo').style.display='none';
			$('spanOrientador').style.display='none';
			$('spanInicio').style.display='none';
		} else {
			$('trCurriculo').style.display='table-row';
			$('spanOrientador').style.display='inline';
			$('spanInicio').style.display='inline';
		}
}
/*
function escolherInstituicao(rede) {
	if (rede.checked) {
		$('instituicao').show();
	} else {
		$('instituicao').hide();
	}
	$('formDiscenteStricto:checkAlunoOutraInstituicaoe').focus();
}
escolherInstituicao($('formDiscenteStricto:checkAlunoOutraInstituicao'));
*/

</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>