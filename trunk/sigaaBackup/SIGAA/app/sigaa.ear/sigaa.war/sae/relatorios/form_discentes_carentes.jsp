<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == false}">
	<h2><ufrn:subSistema/> > Relatório de Discentes Prioritários que solicitaram Bolsa Auxílio </h2>
</c:if>

<c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == true}">
 	<h2><ufrn:subSistema/> > Relatório de Bolsistas (SIPAC) e Situação de Carência</h2>
 </c:if>
	<h:form id="form">
	<div class="descricaoOperacao">
		<p>Caro usuário,</p><br>
		<p>Este relatório traz os bolsistas prioritários ativos no SIPAC. <br/>
		</p> 
	</div>
	<table class="formulario" style="width: 60%">
			
			<caption>Informe os critérios para a emissão do relatório</caption>
	
			<c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == false}">
				<tr>
					<th class="required">Tipo da Bolsa:</th>
					<td colspan="2"> 
						<h:selectOneMenu value="#{relatoriosSaeMBean.tipoBolsaAuxilio.id}" style="width:400px;">
							<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
							<f:selectItems value="#{relatoriosSaeMBean.allTiposBolsas}" />
						</h:selectOneMenu>
					</td>
				</tr>
					
				<tr>
					<th class="required">Ano-Período:</th>
					<td>
						<h:inputText id="ano" 
							value="#{relatoriosSaeMBean.ano}"
							onkeyup="return formatarInteiro(this);" size="4" maxlength="4" /> - 
						
						<h:inputText id="semestre" 
									value="#{relatoriosSaeMBean.periodo}" 
									onkeyup="return formatarInteiro(this);" size="1" maxlength="1" />
					</td>
				</tr>
				
				<tr>
					<th>
						Curso:
					</th>
					<td>
						<h:selectOneMenu id="cursos" value="#{relatoriosSaeMBean.curso.id}" style="width:400px;"
							onfocus = "getEl('formulario:checkCurso').dom.checked = true;">
							<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
							<f:selectItems value="#{relatoriosSaeMBean.allCursos}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioDiscentesSolicitaramCarentesAnoPeriodo}" value="Emitir Relatório" />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			</c:if>
			
			<c:if test="${relatoriosSaeMBean.relatorioBolsasSIPAC == true}">
				<tr>
					<th class="required">Tipo da Bolsa:</th>
					<td> 
						<h:selectOneMenu value="#{relatoriosSaeMBean.tipoBolsaSIPAC.id}" style="width:400px;">
							<f:selectItem itemLabel="-- Selecione --" itemValue="0"/>
							<f:selectItems value="#{relatoriosSaeMBean.allTiposBolsasSIPAC}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioDiscentesSituacaoCarenciaSIPAC}" value="Emitir Relatório"  />
							<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			</c:if>
		
	</table>
	
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br/>
	</center>
		
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>