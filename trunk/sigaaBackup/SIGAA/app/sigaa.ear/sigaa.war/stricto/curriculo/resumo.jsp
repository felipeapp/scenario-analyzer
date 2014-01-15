<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<style>
<!--
table.subFormulario tr.destaque td{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}
table {
	border-collapse: collapse;
}
table.subFormulario {
	width: 100%;
	padding-bottom: 20px;
}
table.formulario th {
	font-weight: bold;
}
-->
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h2 class="title"><ufrn:subSistema /> > Estrutura Curricular de Matrizes Curriculares &gt; Resumo</h2>
	
		<h:form id="formulario">
		
		<table class="formulario" width="80%" >
			<caption class="formulario">Dados do Curr�culo</caption>
			<tr>
				<th width="33%">C�digo:</th>
				<td><h:outputText value="#{curriculo.obj.codigo }" /></td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td><h:outputText value="#{curriculo.obj.curso.descricao}" /></td>
			</tr>
			<tr>
				<th>Per�odo Letivo de Entrada em Vigor:</th>
				<td><h:outputText value="#{curriculo.obj.anoEntradaVigor}.#{curriculo.obj.periodoEntradaVigor }" /></td>
			</tr>
			<tr>
				<th>Carga Hor�ria Por Per�odo Letivo:</th>
				<td>
					<h:outputText value="#{curriculo.obj.chMinimaSemestre}"/>h
				</td>
			</tr>
			<tr>	
				<td colspan="2" class="subFormulario">Carga Hor�ria</td>
			</tr>
			<tr>
				<th>Total M�nima:</th>
				<td><h:outputText value="#{curriculo.obj.chTotalMinima}" />h</td>
			</tr>
			<tr>
				<th>Optativas M�nima:</th>
				<td><h:outputText value="#{curriculo.obj.chOptativasMinima}" />h</td>
			</tr>
			<tr>	
				<td colspan="2" class="subFormulario">Cr�ditos Por Per�odo Letivo</td>
			</tr>
			<tr>	
				<th >M�nimo: </th>
				<td>
					<h:outputText value="#{curriculo.obj.crMinimoSemestre}"/> 
				</td>
			</tr>
			<tr>
				<th >M�dio: </th>
				<td>
					<h:outputText  value="#{curriculo.obj.crIdealSemestre}"/> 
				</td>
			</tr>
			<tr>
				<th >M�ximo: </th>
				<td>
					<h:outputText value="#{curriculo.obj.crMaximoSemestre}" /> 
				</td>
			</tr>
			<tr>	
				<td colspan="2" class="subFormulario">Prazo Para Conclus�o ${curriculo.graduacao? '(em semestres)' : '(em meses)'}</td>
			</tr>
			<tr>
				<th >M�nimo:</th>
				<td>
					<h:outputText value="#{curriculo.obj.semestreConclusaoMinimo}" /> 
				</td>
			</tr>
			<tr>
				<th >Regulamentar:</th>
				<td>
					<h:outputText value="#{curriculo.obj.semestreConclusaoIdeal}" /> 
				</td>
			</tr>
			<tr>
				<th >M�ximo:</th>
				<td>
					<h:outputText value="#{curriculo.obj.semestreConclusaoMaximo}" /> 
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="listagem" width="100%" >
						<caption>Disciplinas do Curr�culo</caption>
						<thead>
							<tr>
								<th>Componente</th>
								<th>Status</th>
							</tr>
						</thead>
						<c:set value="0" var="ch" />
						<c:set value="-1" var="areaAtual" />
						<tbody>				
						<c:forEach items="${curriculo.obj.curriculoComponentes}" var="cc" varStatus="status" >
							<c:set value="${ch + cc.componente.chTotal }" var="ch" />
							<c:if test="${areaAtual != cc.areaConcentracao.id}">
								<c:set value="${cc.areaConcentracao.id}" var="areaAtual" />		
								<tr style="background-color: #C8D5EC;">				
									<td colspan="4"><b>�rea de Concentra��o: ${cc.areaConcentracaoDescricao }</b></td>
								</tr>
							</c:if>
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
								<td>${cc.componente.descricao}</td>
								<td><i>${(cc.obrigatoria)?'Obrigat�ria':'Optativa'}</i></td>
							</tr>
						</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="cadastrar" action="#{curriculo.cadastrarCurriculo}" rendered="#{!curriculo.readOnly || curriculo.confirmButton == 'Remover' || curriculo.confirmButton == 'Ativar' || curriculo.confirmButton == 'Inativar'}"
						value="#{curriculo.confirmButton}" />
					<c:if test="${curriculo.readOnly}">
						<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/> 	
					</c:if>
					<h:commandButton id="cancelar" action="#{curriculo.cancelar}" rendered="#{!curriculo.readOnly}"
						value="Cancelar" onclick="#{confirm}" />
					<h:commandButton id="voltar" action="#{curriculo.telaDadosGerais}" rendered="#{!curriculo.readOnly}"
						value="<< Dados Gerais" />
					<h:commandButton id="componentes" action="#{curriculo.telaComponentes}" rendered="#{!curriculo.readOnly}"
						value="<< Componentes" />
				</td>
			</tr>
			</tfoot>
		</table>
		<br/>		
		<c:if test="${curriculo.pedirSenha || !curriculo.readOnly}">
		 	<c:set var="exibirApenasSenha" value="true" scope="request"/>
			<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	 	</c:if>
	 	</h:form>
	 	<br>
		<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
			class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
		<br>
		</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
