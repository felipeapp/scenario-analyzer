<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2><ufrn:subSistema/> > Relatório de Desempenho de Bolsistas</h2>

	<h:form id="form">
		<table class="formulario" style="width: 95%">
			<caption>Informe os critérios para a emissão do relatório</caption>

			<tr>
				<th>Nível de Ensino: 
					<c:if test="${relatoriosSaeMBean.obrigatorio}">
						<html:img page="/img/required.gif"/>
					</c:if>
				</th> 
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.nivel}"  id="nivel">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue=""/>
						<f:selectItems value="#{nivelEnsino.allCombo}"/>
						<a4j:support event="onchange" action="#{relatoriosSaeMBean.carregarCursos}" reRender="curso" />
					</h:selectOneMenu>
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>
				</td>
			</tr>
			
			<tr>
				<th>Curso:
					<c:if test="${relatoriosSaeMBean.obrigatorio}">
						<html:img page="/img/required.gif"/>
					</c:if>
				</th>
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.curso.id}"  id="curso">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{relatoriosSaeMBean.allCursos}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="required">Tipo de Bolsa: </th>
				<td>
					<h:selectOneMenu value="#{relatoriosSaeMBean.tipoBolsaAuxilio.id}"  id="tipoBolsa">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{relatoriosSaeMBean.allTiposBolsasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		
			<tr>
				<th class="required">Ano e Período sem a bolsa: </th>
				<td>
					<h:inputText id="anoAntes" value="#{relatoriosSaeMBean.anoSem}"
					size="3" maxlength="4" onkeyup="formatarInteiro(this)" />.<h:inputText
					id="semestreAntes" value="#{relatoriosSaeMBean.periodoSem}" size="1"
					maxlength="1" onkeyup="formatarI nteiro(this)" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Ano e Período com a bolsa: </th>
				<td>
					<h:inputText id="ano" value="#{relatoriosSaeMBean.ano}"
					size="3" maxlength="4" onkeyup="formatarInteiro(this)" />.<h:inputText
					id="semestre" value="#{relatoriosSaeMBean.periodo}" size="1"
					maxlength="1" onkeyup="formatarInteiro(this)" />
				</td>
			</tr>
			
			<tr>
				<th>Matrícula do Discente: </th>
				<td>
					<h:inputText id="matriculaDiscente" value="#{relatoriosSaeMBean.matricula}"
						size="14" maxlength="12" onkeyup="formatarInteiro(this)" immediate="true" 
						valueChangeListener="#{relatoriosSaeMBean.verificaObrigatoriedade}" onblur="submit()"/>
				</td>
			</tr>
			
			<tr>
				<th>Nome do Discente: </th>
				<td>
					<h:inputText value="#{relatoriosSaeMBean.nome}" size = "60" maxlength="60" id="nomeDiscente"  />
				</td>
			</tr>
			

			<tfoot>
				<tr>	
					<td colspan="2">
						<h:commandButton action="#{relatoriosSaeMBean.gerarRelatorioDesempenho}" value="Emitir Relatório" />
						<h:commandButton action="#{relatoriosSaeMBean.cancelar}" value="Cancelar" immediate="true" 
						 	onclick="return confirm('Deseja cancelar a operação? Todos os dados digitados não salvos serão perdidos!');"> 
						</h:commandButton>
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<div align="center">
			<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>