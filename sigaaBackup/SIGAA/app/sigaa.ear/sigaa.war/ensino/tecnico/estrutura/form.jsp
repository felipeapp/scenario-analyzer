<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Dados Gerais </h2>
<a4j:keepAlive beanName="estruturaCurricularTecnicoMBean"/>	
<h:form id="form">
	<table class="formulario" style="width: 100%">
	  <caption>Dados Gerais da Estrutura Curricular</caption>
		<h:inputHidden value="#{estruturaCurricularTecnicoMBean.obj.id}" />
		<tbody>
			<tr>
				<th width="25%" class="obrigatorio">Código da Estrutura:</th>
				<td colspan="4"><h:inputText value="#{estruturaCurricularTecnicoMBean.obj.codigo}" size="5" 
					onkeyup="return formatarInteiro(this);" maxlength="10"/></td>
			</tr>
			<tr>
				<th class="obrigatorio">Curso:</th>
				<td colspan="4">
					<h:selectOneMenu value="#{estruturaCurricularTecnicoMBean.obj.cursoTecnico.id}" id="curso">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{curso.allCursoNivelAtualCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Prazo de Conclusão:</th>
				<td  colspan="4">
					<h:selectOneMenu value="#{estruturaCurricularTecnicoMBean.obj.unidadeTempo.id}" 
					id="unidadeTempo" valueChangeListener="#{estruturaCurricularTecnicoMBean.changeUnidadeTempo}">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{estruturaCurricularTecnicoMBean.allUnidadeTempo}" />
				 		<a4j:support event="onchange" reRender="form" /> 
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Mínimo:</th>
				<td colspan="4"><h:inputText value="#{estruturaCurricularTecnicoMBean.obj.prazoMinConclusao}" size="3" 
					maxlength="2" id="prazoMin" onkeyup="return formatarInteiro(this);"/>
					${estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}(s)</td>
			</tr>
			<tr>
				<th class="obrigatorio">Máximo:</th>
				<td colspan="4"><h:inputText value="#{estruturaCurricularTecnicoMBean.obj.prazoMaxConclusao}" size="3"
					maxlength="2" id="prazoMax" onkeyup="return formatarInteiro(this);"/>
					${estruturaCurricularTecnicoMBean.obj.unidadeTempo.descricao}(s)</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano - Período de Entrada em Vigor:</th>
				<td colspan="4"><h:inputText value="#{estruturaCurricularTecnicoMBean.obj.anoEntradaVigor}" size="4" maxlength="4" 
				onkeyup="return formatarInteiro(this);"/> - <h:inputText value="#{estruturaCurricularTecnicoMBean.obj.periodoEntradaVigor}" 
				size="2" maxlength="1" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<th>Ativo:</th>
			 	<td colspan="4">
			 		<h:selectOneRadio value="#{estruturaCurricularTecnicoMBean.obj.ativa}">
						<f:selectItem itemValue="false" itemLabel="Não" />
						<f:selectItem itemValue="true" itemLabel="Sim" />
		    		</h:selectOneRadio>
		  	 	</td>
			</tr>
			<tr>
				<th>Turno:</th>
				<td colspan="4">
					<h:selectOneMenu value="#{estruturaCurricularTecnicoMBean.obj.turno.id}" id="turno">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
				 		<f:selectItems value="#{turno.allAtivosCombo}" /> 
					</h:selectOneMenu>
				</td>
			</tr>	
			<tr>
				<th> Mínimo de Componentes Complementares: </th>
				<td colspan="4" width="16%">
					<h:inputText size="4" maxlength="4" value="#{estruturaCurricularTecnicoMBean.obj.chOptativasMinima}"  
						converter="#{ intConverter }" onkeyup="formatarInteiro(this);" styleClass="numerico" id="inputChMaxCompEletivos"/> 
						ch 
				</td>
			</tr>			
		</tbody>
		<tfoot>
		   <tr>
				<td colspan="6">
					<c:choose>
						<c:when test="${estruturaCurricularTecnicoMBean.obj.id > 0}">
							<h:commandButton value="<< Voltar" action="#{estruturaCurricularTecnicoMBean.listar}" id="voltar" rendered="#{estruturaCurricularTecnicoMBean.obj.id > 0}" />
							<h:commandButton value="Cancelar" action="#{estruturaCurricularTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
							<h:commandButton value="Avançar >>" action="#{estruturaCurricularTecnicoMBean.submeterDadosGerais}" id="cadastrar" />
						</c:when>
						<c:otherwise>
							<h:commandButton value="Avançar >>" action="#{estruturaCurricularTecnicoMBean.submeterDadosGerais}" id="cadastrar" />
							<h:commandButton value="Cancelar" action="#{estruturaCurricularTecnicoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
						</c:otherwise>
					</c:choose>
				</td>
		   </tr>
		</tfoot>
	</table>
	
	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>