<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<f:view>
	${componenteCurricular.carregarComponente}

	<c:if test="${componenteCurricular.obj != null}">
		<table class="listagem" style="width:100%">
			<caption>Dados Gerais do Componente Curricular</caption>
			<tr>
				<th width="30%">C�digo:</th>
				<td align="left"><h:outputText value="#{componenteCurricular.obj.codigo}" /></td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td><h:outputText value="#{componenteCurricular.obj.detalhes.nome}" /></td>
			</tr>
			<tr>
				<th>Unidade Respons�vel:</th>
				<td><h:outputText value="#{componenteCurricular.obj.unidade.nome}" /></td>
			</tr>
			<tr>
				<th>Tipo do Componente Curricular:</th>
				<td><h:outputText value="#{componenteCurricular.obj.tipoComponente.descricao}" /></td>
			</tr>
			
			<c:if test="${componenteCurricular.obj.atividade}">
				<tr>
					<th>Tipo de Atividade:</th>
					<td><h:outputText value="#{componenteCurricular.obj.tipoAtividade.descricao}" /></td>
				</tr>
			</c:if>			
			<c:if test="${not empty componenteCurricular.obj.programa}">
				<tr>
					<th> Programa: </th>
					<td>
						<h:form target="_blank">
							
							<input type="hidden" value="${componenteCurricular.obj.id}" name="idComponente" />
							<h:commandButton alt="Ver Componente Curricular" image="/img/report.png" 
							 	style="background: white; border: none;" 
								title="Ver Componente Curricular"
								action="#{programaComponente.gerarRelatorioPrograma}" />
							
							<h:commandButton alt="Ver Componente Curricular" 
							 	style="background: #F8F8FF; border: none; color: #0000CD" 
							 	value="Consultar Programa do Componente"
								title="Ver Componente Curricular"
								action="#{programaComponente.gerarRelatorioPrograma}" />

						</h:form>
					</td>
			</c:if>			
			
			<tr>
				<td class="subFormulario" colspan="2" style="text-align: center;">Carga Hor�ria</td>
			</tr>
			
			<c:if test="${componenteCurricular.obj.disciplina or componenteCurricular.obj.moduloOuAtividadeColetiva}">
			<tr>
				<th>Cr�ditos Te�ricos:</th>
				<td>
					<h:outputText value="#{componenteCurricular.obj.detalhes.crAula}" /> crs.
					(${componenteCurricular.obj.detalhes.chAula} horas)
				</td>
			</tr>
			<tr>
				<th>Cr�ditos Pr�ticos:</th>
				<td>
					<h:outputText value="#{componenteCurricular.obj.detalhes.crLaboratorio}" /> crs. 
					(${componenteCurricular.obj.detalhes.chLaboratorio} horas)
				</td>
			</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.atividade}">
				<tr>
					<th>Carga Hor�ria Te�rica:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> h.</td>
				</tr>
				<tr>
					<th>Carga Hor�ria Pr�tica:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chLaboratorio}" /> h.</td>
				</tr>
			</c:if>
			<c:if test="${componenteCurricular.obj.bloco}">
				<tr>
					<th>Carga Hor�ria Total:</th>
					<td><h:outputText value="#{componenteCurricular.obj.detalhes.chAula}" /> h.</td>
				</tr>
			</c:if>
			
			<tr>
				<td class="subFormulario" colspan="2" style="text-align: center;">Pr�-requisitos, Co-Requisitos e Equival�ncias</td>
			</tr>
			<tr>
				<th>Pr�-Requisitos:</th>
				<td>
					<h:outputText value="#{componenteCurricular.preRequisitoForm}" escape="false"/>
				</td>
			</tr>
			<tr>
				<th>Co-Requisitos:</th>
				<td>
					<h:outputText value="#{componenteCurricular.coRequisitoForm}" escape="false"/>
				</td>
			</tr>
			<tr>
				<th>Equival�ncias:</th>
				<td>
					<h:outputText value="#{componenteCurricular.equivalenciaForm}" escape="false"/>
				</td>
			</tr>

			
			<!-- Dados do bloco -->
			<c:if test="${componenteCurricular.obj.bloco }">
				<tr>
					<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption>Sub-unidades do Bloco</caption>
						<c:forEach items="${componenteCurricular.obj.subUnidades}" var="unid">
							<tr>
								<td>${unid.nome}</td>
							</tr>
						</c:forEach>
					</table>
					</td>
				</tr>
			</c:if>
			
			
			<c:if test="${not componenteCurricular.obj.bloco }">
				<tr>
					<td colspan="2" class="subFormulario" style="text-align: center;"> Ementa/Descri��o </td>
				</tr>
				<tr>
					<td colspan="2">
						<p style="padding: 5px; line-height: 1.2em;">
							<h:outputText value="#{componenteCurricular.obj.detalhes.ementa}" converter="convertTexto" 
									rendered="#{componenteCurricular.obj.detalhes.ementa != null}"/> 
							<h:outputText value="Ementa n�o cadastrada" rendered="#{componenteCurricular.obj.detalhes.ementa == null}"/>
						</p>
					</td>
				</tr>
			</c:if>			
			
			<tr>
				<td colspan="2" class="subFormulario" style="text-align: center;">Outras informa��es</td>
			</tr>
			<tr>
				<th>Matricul�vel "On-Line":</th>
				<td>
					<ufrn:format type="bool_sn" valor="${componenteCurricular.obj.matriculavel}" />
				</td>
			</tr>
			<tr>
				<th>Pode criar turma sem solicita��o:</th>
				<td>
					<ufrn:format type="bool_sn" valor="${componenteCurricular.obj.turmasSemSolicitacao}" />
				</td>
			</tr>
			<tr>
				<th>Quantidade de Avalia��es:</th>
				<td>
					${componenteCurricular.obj.numUnidades}
				</td>
			</tr>			
			<tr>
				<th>Possui subturmas:</th>
				<td>
					<ufrn:format type="bool_sn" valor="${componenteCurricular.obj.aceitaSubturma}" />
				</td>
			</tr>	
			<tr>
				<th>Permite Turma com Flexibilidade de Hor�rio:</th>
				<td>
					<ufrn:format type="bool_sn" valor="${componenteCurricular.obj.permiteHorarioFlexivel}" />	
				</td>
			</tr>	
		</table>
	</c:if>
</f:view>
