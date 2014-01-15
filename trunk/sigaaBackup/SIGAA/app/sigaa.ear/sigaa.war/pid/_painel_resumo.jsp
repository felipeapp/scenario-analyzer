<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<rich:panel header="QUADRO RESUMO" id="resumo" styleClass="painelResumo">
	<c:set var="horasInformadas" value="#{_pidBean.obj.totalGrupoEnsino + _pidBean.obj.totalGrupoOutrasAtividades}" />
	<c:set var="diferencaHoras" value="#{_pidBean.obj.servidor.regimeTrabalho - horasInformadas}" />

	<t:htmlTag value="table">
		<t:htmlTag value="tr" styleClass="grupo">
			<t:htmlTag value="th"> I - Carga horária total de ensino: </t:htmlTag> 
			<td> 
				<h:outputText value="#{_pidBean.obj.totalGrupoEnsino}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>
				<f:verbatim escape="false"> h</f:verbatim>
			</td>
		</t:htmlTag>
		
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Carga horária de ensino: </t:htmlTag> 
			<td> 
				<h:outputText value="#{_pidBean.obj.totalChTurmas}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h
			</td>
		</t:htmlTag>
		
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Carga horária de orientações de atividades: </t:htmlTag> 
			<td> 
				<h:outputText value="#{_pidBean.obj.totalChOrientacaoAtividades}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h
			</td>
		</t:htmlTag>
		
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Carga horária de orientações e acompanhamentos: </t:htmlTag> 
			<td> 
				<h:outputText value="#{_pidBean.obj.totalChOrientacoes}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h
			</td>
		</t:htmlTag>
		
		<t:htmlTag value="tr" styleClass="grupo">
			<t:htmlTag value="th"> II - Total de pesquisa, extensão, e outras atividades: </t:htmlTag> 
	 		<td> 
				<h:outputText value="#{_pidBean.obj.totalGrupoOutrasAtividades}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h
		 	</td>
	 	</t:htmlTag>
	 	
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Outras atividades de ensino: </t:htmlTag>
			<td> 
				<h:outputText value="#{_pidBean.obj.chOutrasAtividades.chSemanalOutrasAtividadesEnsino}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h<br/>
			</td>
		</t:htmlTag>
			
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Pesquisa e produção acadêmica: </t:htmlTag>
			<td> 
				<h:outputText value="#{_pidBean.obj.chProjeto.chPesquisa}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h<br/>
			</td>
		</t:htmlTag>	
		
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Extensão e outras atividades: </t:htmlTag>
			<td> 
				<h:outputText value="#{_pidBean.obj.chProjeto.chExtensao}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h<br/>
			</td>
		</t:htmlTag>
			
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Funções administrativas: </t:htmlTag>
			<td> 
				<h:outputText value="#{_pidBean.obj.chTotalAdministracao}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h<br/>
			</td>
		</t:htmlTag>
		
		<t:htmlTag value="tr" styleClass="item">
			<t:htmlTag value="th"> Outras atividades: </t:htmlTag>
			<td> 
				<h:outputText value="#{_pidBean.obj.chOutrasAtividades.chSemanalOutrasAtividades}">
					<f:convertNumber maxFractionDigits="1" groupingUsed="false" /> 
				</h:outputText>h<br/>
			</td>
		</t:htmlTag>
		
		<t:htmlTag value="tr" styleClass="grupo total">
			<t:htmlTag value="th"> Carga horária total informada: </t:htmlTag>
	 		<td> <fmt:formatNumber type="number" maxFractionDigits="1" groupingUsed="false" value="${horasInformadas}" />h </td>
	 	</t:htmlTag>
		
	<c:if test="${diferencaHoras > 0}">
		<t:htmlTag value="tr" styleClass="grupo pendente">
			<t:htmlTag value="th"> Carga horária pendente de definição: </t:htmlTag>
	 		<td> <fmt:formatNumber type="number" maxFractionDigits="1" groupingUsed="false" value="${diferencaHoras}" />h </td>
	 	</t:htmlTag>
	</c:if>
	</t:htmlTag>
</rich:panel>
