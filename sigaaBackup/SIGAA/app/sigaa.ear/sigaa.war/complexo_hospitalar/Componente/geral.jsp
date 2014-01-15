<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>

<%@page import="br.ufrn.sigaa.ensino.dominio.ComponenteCurricular"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.TipoAtividade"%>

<c:set var="AGUARDANDO_CONFIRMACAO" value="<%= ComponenteCurricular.AGUARDANDO_CONFIRMACAO %>" />

<f:view>
	<h:outputText value="#{componenteCurricular.create}"/>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h2 class="title"><ufrn:subSistema /> &gt; Cadastro de Componente
	Curricular &gt; Dados Gerais</h2>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption class="formulario">Dados Gerais do Componente
			Curricular</caption>
			<tr>
				<th class="required">Unidade Respons�vel:</th>
				<td><c:choose>
					<%--<c:when test="${componenteCurricular.podeAlterarUnidade || (componenteCurricular.obj.graduacao and componenteCurricular.obj.id == 0) }">--%>
					<c:when
						test="${componenteCurricular.obj.id == 0 && componenteCurricular.podeAlterarUnidade }">
						<h:selectOneMenu id="unidades"
							value="#{componenteCurricular.obj.unidade.id}"
							valueChangeListener="#{componenteCurricular.selecionarUnidade}"
							onchange="submit()" style="width: 95%;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{componenteCurricular.possiveisUnidades}" />
						</h:selectOneMenu>
					</c:when>
					<c:otherwise>
							${componenteCurricular.obj.unidade.nomeMunicipio }
						</c:otherwise>
				</c:choose></td>
			</tr>
			<tr>
				<th width="30%" class="required">C�digo:</th>
				<td><h:inputText id="codigo"
					value="#{componenteCurricular.obj.codigo}" size="10" maxlength="7"
					onkeyup="CAPS(this)" readonly="#{ componenteCurricular.readOnly || componenteCurricular.obj.statusInativo != AGUARDANDO_CONFIRMACAO }"
					disabled="#{ componenteCurricular.readOnly || componenteCurricular.obj.statusInativo != AGUARDANDO_CONFIRMACAO }" />
				<c:if test="${not empty codigoSugerido}">
					<button type="button"
						onclick="$('form:codigo').value = $('codigoSugerido').innerHTML">&lt;&lt;</button>
					<span style="color: gray;" id="codigoSugerido">${codigoSugerido}</span>
				</c:if> 
				<ufrn:help img="/img/ajuda.gif">O preenchimento autom�tico do c�digo � uma sugest�o do sistema por um c�digo
						dispon�vel para um componente dessa unidade</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText id="nome"
					value="#{componenteCurricular.obj.detalhes.nome}" size="90"
					maxlength="149" onkeyup="CAPS(this)"
					disabled="#{ (componenteCurricular.obj.graduacao && 
					!acesso.administradorDAE && componenteCurricular.readOnly) || (componenteCurricular.obj.stricto && !acesso.ppg) }" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Carga Hor�ria</td>
			</tr>
			<tr>
				<th id="labelTeorico">Cr�ditos Te�ricos:</th>
				<td>
					<h:inputText id="craula" value="#{componenteCurricular.crAula}" 
								size="4" maxlength="4"
								onkeyup="calcularCHTeorico(); return formatarInteiro(this);" 
								disabled="#{ (componenteCurricular.obj.stricto && acesso.programaStricto) || ( componenteCurricular.obj.graduacao  && componenteCurricular.readOnly && !acesso.administradorDAE) }" />
					<span id="chteorico"></span>
					<h:inputHidden id="chaula" value="#{componenteCurricular.obj.detalhes.chAula}" />
				</td>
			</tr>
			<tr>
				<th id="labelPratico">Cr�ditos Pr�ticos:</th>
				<td>
					<h:inputText id="crlab"	value="#{componenteCurricular.crLaboratorio}"
								size="4" maxlength="4"
								onkeyup="calcularCHPratico(); return formatarInteiro(this);"
								disabled="#{ (componenteCurricular.obj.stricto && acesso.programaStricto) || ( componenteCurricular.obj.graduacao  && componenteCurricular.readOnly && !acesso.administradorDAE) }" />
					<span id="chpratico"></span>
					<h:inputHidden id="chlab" value="#{componenteCurricular.obj.detalhes.chLaboratorio}" /></td>
			</tr>

			<tr  id="trChDedicadaDocente">
				<th>Carga Hor�ria do Docente:</th>
				<td>
					<h:inputText id="chDedicadaDocente"	value="#{componenteCurricular.chDedicadaDocente}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>

			<c:if
				test="${componenteCurricular.obj.id > 0 and componenteCurricular.obj.detalhes.crEstagio > 0 }">
				<tr>
					<th>Cr�ditos de Est�gio:</th>
					<td>${componenteCurricular.obj.detalhes.crEstagio} cr�ditos</td>
				</tr>
			</c:if>
			
			<c:if test="${componenteCurricular.obj.id > 0 and componenteCurricular.obj.detalhes.crEstagio > 0 }">
				<tr>
					<th>Cr�ditos de Est�gio:</th>
					<td>${componenteCurricular.obj.detalhes.crEstagio} cr�ditos</td>
				</tr>
			</c:if>

			<tr>
				<td colspan="2" class="subFormulario">Pr�-requisitos,
				Co-Requisitos e Equival�ncias</td>
			</tr>
			<tr>
				<td colspan="2">
				<p
					style="text-align: center; padding: 5px; font-style: italic; color: #D00">
				<ufrn:help img="/img/ajuda.gif">
						Exemplo: ( ( DIM0052 ) E ( DIM0301 OU DIM0053 ) )
					</ufrn:help> <b>Aten��o!</b> Todas as express�es de pr�-requisitos,
				co-requisitos e equival�ncias devem ser cercadas por par�nteses.</p>
				</td>
			</tr>
			<tr>
				<th>Pr�-Requisitos:</th>
				<td><h:inputText id="pre-req"
					value="#{componenteCurricular.preRequisitoForm}" size="90" maxlength="200"
					disabled="#{acesso.programaStricto}" /></td>
			</tr>
			<tr>
				<th>Co-Requisitos:</th>
				<td><h:inputText id="co-req"
					value="#{componenteCurricular.coRequisitoForm}" size="90" maxlength="200"
					disabled="#{acesso.programaStricto}" /></td>
			</tr>
			<tr>
				<th>Equival�ncias:</th>
				<td><h:inputText id="equiv"
					value="#{componenteCurricular.equivalenciaForm}" size="90" maxlength="200"
					disabled="#{acesso.programaStricto}" /></td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Outras informa��es</td>
			</tr>
			
			
			
			<tr><td colspan="2"><table width="100%">
			
				<tr id="checkMatriculavel">
					<th width="25%">
						<ufrn:help img="/img/ajuda.gif">Desmarque essa op��o caso alunos n�o possa se matricular nesse componente</ufrn:help>
						Matricul�vel "On-Line":
					</th>
					<td>
						<h:selectOneRadio value="#{componenteCurricular.obj.matriculavel}" styleClass="noborder" disabled="#{acesso.programaStricto}" > 
							<f:selectItem itemValue="true" itemLabel="Sim" />
							<f:selectItem itemValue="false" itemLabel="N�o"/>
						</h:selectOneRadio>
					</td>
				</tr>
				<c:if test="${not (sessionScope.acesso.coordenadorCursoGrad and componenteCurricular.portalCoordenadorGraduacao)}">
					<tr id="tipoAtividadeNota">
						<th width="25%">
							<ufrn:help img="/img/ajuda.gif">Desmarque essa op��o caso a atividade n�o precise de m�dia final para sua consolida��o ou valida��o</ufrn:help>						
							Precisa de ${ (sessionScope.nivel == 'S') ? 'Conceito' : 'Nota' }:
						</th>
						<td>		 
							<h:selectOneRadio value="#{componenteCurricular.obj.necessitaMediaFinal}" disabled="#{acesso.programaStricto}">
								<f:selectItems value="#{componenteCurricular.simNao}"  />
							</h:selectOneRadio>
						</td>
					</tr>
				</c:if>					
				<tr id="checkSolicitacaoTurma">
					<th  width="25%">
						<ufrn:help img="/img/ajuda.gif">Marque esta op��o caso seja poss�vel criar turma deste
							componente sem a necessidade de haver uma solicita��o de turma.</ufrn:help>
						Pode criar turma sem solicita��o:
					</th>
					<td>
						<h:selectOneRadio value="#{componenteCurricular.obj.turmasSemSolicitacao}" 
							styleClass="noborder" disabled="#{acesso.programaStricto}" > 
							<f:selectItem itemValue="true" itemLabel="Sim"/>
							<f:selectItem itemValue="false" itemLabel="N�o"/>
						</h:selectOneRadio>
					</td>
				</tr>
				<tr id="tabelaNecessitaOrientador">
					<th width="25%">
						<ufrn:help img="/img/ajuda.gif">Marque esta op��o caso a atividade complementar tenha orientador.</ufrn:help>
						Necessita de Orientador:
					</th>
					<td>
						<t:selectOneRadio value="#{componenteCurricular.obj.temOrientador}" forceId="true"
							styleClass="noborder" id="checkNecessitaOrientador" disabled="#{acesso.programaStricto}" > 
							<f:selectItem itemValue="true" itemLabel="Sim"/>
							<f:selectItem itemValue="false" itemLabel="N�o"/>
						</t:selectOneRadio>
						<%-- 
						<h:selectBooleanCheckbox id="checkNecessitaOrientador"
						value="#{componenteCurricular.obj.temOrientador}"
						styleClass="noborder" disabled="#{acesso.programaStricto}" /> 
						--%>
					</td>
				</tr>
				<c:if test="${!componenteCurricular.stricto}">
					<tr id="trSubturma">
						<th width="25%"><ufrn:help img="/img/ajuda.gif">Marque esta op��o caso seja um componente que permita a cria��o de subturmas.</ufrn:help>Possui subturmas:</th>
						<td nowrap="nowrap">
							<h:selectOneRadio value="#{componenteCurricular.obj.aceitaSubturma}" 
							styleClass="noborder"> 
								<f:selectItem itemValue="true" itemLabel="Sim"/>
								<f:selectItem itemValue="false" itemLabel="N�o"/>
							</h:selectOneRadio>
						</td>
					</tr>
					
					<tr id="trExigeHorarioTurmaEmTurmas">
						<th width="25%"><ufrn:help img="/img/ajuda.gif">Marque esta op��o caso n�o seja poss�vel criar turmas deste componente sem informar hor�rio.</ufrn:help>Exige Hor�rio:</th>
						<td nowrap="nowrap">
							<h:selectOneRadio value="#{componenteCurricular.obj.exigeHorarioEmTurmas}" 
							styleClass="noborder" id="checkExigeHorarioEmTurmas" > 
								<f:selectItem itemValue="true" itemLabel="Sim"/>
								<f:selectItem itemValue="false" itemLabel="N�o"/>
							</h:selectOneRadio>
						</td>
					</tr>
					
					<tr id="trPermiteChCompartilhada">
						<th width="25%"><ufrn:help img="/img/ajuda.gif">Marque esta op��o caso a soma da carga hor�ria de todos os docentes possa ultrapassar a carga hor�ria do componente curricular.</ufrn:help>Permite Ch Compartilhada:</th>
						<td nowrap="nowrap">
							<h:selectOneRadio value="#{componenteCurricular.obj.detalhes.permiteChCompartilhada}" 
							styleClass="noborder" id="checkPermiteChCompartilhada" > 
								<f:selectItem itemValue="true" itemLabel="Sim"/>
								<f:selectItem itemValue="false" itemLabel="N�o"/>
							</h:selectOneRadio>
						</td>
					</tr>
					
					<tr id="linhaQtdAvaliacoes">
						<th>Quantidade de Avalia��es:</th>
						<td><h:selectOneMenu id="numunidades"
							value="#{componenteCurricular.obj.numUnidades}">
							<f:selectItems
								value="#{componenteCurricular.numUnidadesPossiveis}" />
						</h:selectOneMenu></td>
					</tr>
				</c:if>
			</table></td></tr>
			
			<tr>
				<th valign="top" id="campoEmenta" class="required">Descri��o:</th>
				<td><h:inputTextarea id="ementa"
					value="#{componenteCurricular.obj.detalhes.ementa}"
					disabled="#{componenteCurricular.obj.bloco}" cols="85" rows="4" />
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{componenteCurricular.cancelar}" immediate="true" />
						<h:commandButton value="Avan�ar >>" action="#{componenteCurricular.submeterDadosGerais}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><h:graphicImage url="/img/required.gif"
		style="vertical-align: top;" /><span class="fontePequena">
	Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>
	<script type="text/javascript">
<!--

function mudarTipoSelect() {
		var sel = $('form:tipo');
		//$('form:craula').value = '0';
		//$('form:crlab').value = '0';
		//$('form:chaula').value = '0';
		//$('form:chlab').value = '0';

		var val = sel.options[sel.selectedIndex];
		mudarTipo(val.value);
}

function mudarTipo(valor) {
	if (valor == '<%=TipoComponenteCurricular.ATIVIDADE%>' 
			|| valor == '<%=TipoComponenteCurricular.MODULO%>'
			|| valor == '<%=TipoComponenteCurricular.ATIVIDADE_COLETIVA%>') {
		$('labelTeorico').innerHTML = 'Carga Hor�ria Te�rica:';
		$('labelPratico').innerHTML = 'Carga Hor�ria Pr�tica:';
		$('chteorico').style.visibility = 'hidden';
		$('chpratico').style.visibility = 'hidden';
		$('checkMatriculavel').hide();
		$('checkSolicitacaoTurma').hide();
		$('trSubturma').hide();

	} else {
		$('labelTeorico').innerHTML = 'Cr�ditos Te�ricos:';
		$('labelPratico').innerHTML = 'Cr�ditos Pr�ticos:';
		$('chteorico').style.visibility = 'visible';
		$('chpratico').style.visibility = 'visible';
		//${!acesso.programaStricto}
		<c:if test="${!acesso.programaStricto}">
			$('checkMatriculavel').show();
			$('checkSolicitacaoTurma').show();
			$('trSubturma').show();
		</c:if>
		
	}
	if (valor == '<%=TipoComponenteCurricular.ATIVIDADE%>') {
		$('campoEmenta').innerHTML = 'Descri��o:';
		<c:if test="${not (sessionScope.acesso.coordenadorCursoGrad and componenteCurricular.portalCoordenadorGraduacao)}">
			$('tipoAtividadeNota').style.visibility='visible';
			$('linhaTipoAtividade').style.visibility='visible';
		</c:if>
		$('trExigeHorarioTurmaEmTurmas').style.visibility='hidden';
		$$("#trChDedicadaDocente").each( function(el) { el.show() });
		mudarTipoAtividade();
	} else {
		$('campoEmenta').innerHTML = 'Ementa:';
		<c:if test="${not (sessionScope.acesso.coordenadorCursoGrad and sessionScope.componenteCurricular.portalCoordenadorGraduacao)}">
			$('linhaTipoAtividade').style.visibility='hidden';
			$('tipoAtividadeNota').style.visibility='hidden';
		</c:if>
		$('trExigeHorarioTurmaEmTurmas').style.visibility='visible';
		$$("#trChDedicadaDocente").each( function(el) { el.hide() });
		$$("#tabelaNecessitaOrientador").each( function(el) { el.hide() });
		
		<c:if test="${acesso.algumUsuarioStricto}">
			$('checkSolicitacaoTurma').hide();
		</c:if>
	//	$('linhaTipoAtividadeCompl').style.visibility='hidden';
	}
	// habilitar ementa
	if (valor == '<%=TipoComponenteCurricular.BLOCO%>') {
		$('form:ementa').disabled = true;
		$('form:craula').disabled = true;
		$('form:crlab').disabled = true;
		$('form:ementa').value = '';
	} else {
		<c:if test="${ acesso.ppg || componenteCurricular.obj.id == 0 || acesso.administradorDAE }">
			$('form:ementa').disabled = false;
			$('form:craula').disabled = false;
			$('form:crlab').disabled = false;
			
		</c:if>
	}
}

function mudarTipoAtividade() {
	var sel = $('form:tipoAtividade');
	if (sel.options[sel.selectedIndex].value == '<%=TipoAtividade.COMPLEMENTAR%>') {
		$$("#tabelaNecessitaOrientador").each( function(el) { el.show() });
		$$("#trChDedicadaDocente").each( function(el) { el.hide() });
	} else {
		$$("#tabelaNecessitaOrientador").each( function(el) { el.hide() });
		$$("#trChDedicadaDocente").each( function(el) { el.show() });
	}
}

function calcularCHTeorico() {
	$('form:chaula').value=$('form:craula').value * ${componenteCurricular.horasCreditoPratico};
	$('chteorico').innerHTML = '('+$('form:chaula').value+' h )';
}

function calcularCHPratico() {
	$('form:chlab').value=$('form:crlab').value * ${componenteCurricular.horasCreditoPratico};
	$('chpratico').innerHTML = '('+$('form:chlab').value+' h )';
}
calcularCHPratico();
calcularCHTeorico();
mudarTipoAtividade();

<c:if test="${not (sessionScope.acesso.coordenadorCursoGrad and componenteCurricular.portalCoordenadorGraduacao)}">
	mudarTipoSelect();
</c:if>
<c:if test="${sessionScope.acesso.coordenadorCursoGrad and componenteCurricular.portalCoordenadorGraduacao}">
	mudarTipo('<%=TipoComponenteCurricular.ATIVIDADE%>');
</c:if>
//-->
</script>
	<script type="text/javascript">
		if ($('form:codigo').disabled == false)
			$('form:codigo').focus();
	</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
