<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Relat�rio de Docentes por Semestre</h2>
	<h:form id="busca">
		
		<div class="descricaoOperacao">
			<p>Este relat�rio consiste em listar os docentes e suas disciplinas ministradas agrupando-as por Centro, 
			de acordo com o semestre e filtros selecionados.</p> 
			<p>Para gerar o Relat�rio, � necess�rio inicialmente informar o ano e per�odo e/ou os seguintes campos opcionais:</p>
			
			<ul>
				<li><b>Centro/Unidade Acad�mica Especializada:</b> Corresponde ao Centro ou Unidade Acad�mica Especializada respons�vel pelo departamento, que disciplina � vinculada;</li>
				<li><b>Departamento:</b> Unidade acad�mica que a disciplina � vinculada;</li>
				<li><b>P�lo:</b> P�lo onde a disciplina foi oferecida;</li>
			</ul>
			
		</div>
		
		<table class="formulario" width="80%">
			<caption>Filtros para o Relat�rio</caption>
			
			<tbody>
				<tr>
					<th width="40%" class="obrigatorio">Ano-Per�odo:</th>
					<td>
						<h:inputText value="#{relatorioDocenteDisciplinasMBean.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> .
						<h:inputText value="#{relatorioDocenteDisciplinasMBean.periodo}" id="periodo" size="1" maxlength="1" onkeyup="return formatarInteiro(this);"/>
	 				</td>
				</tr>
				<tr>
					<th> Centro/Unidade Acad�mica Especializada:</th>
					<td>
					<a4j:region>
						<h:selectOneMenu value="#{relatorioDocenteDisciplinasMBean.unidadeResponsavel.id}" id="selectUnidadeResponsavel"
							valueChangeListener="#{relatorioDocenteDisciplinasMBean.changeCentro}">
							<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{relatorioDocenteDisciplinasMBean.allCentrosUnidAcademicaCombo}" />
							<a4j:support event="onchange" reRender="selectDepartamento"/>
						</h:selectOneMenu>	
						<a4j:status>
							<f:facet name="start">
								<h:graphicImage value="/img/indicator.gif" />
							</f:facet>
						</a4j:status>
					</a4j:region>
					</td>
				</tr>	
				<tr>
					<th>Departamento:</th>
					<td>
						<h:selectOneMenu value="#{relatorioDocenteDisciplinasMBean.departamento.id}" id="selectDepartamento">
							<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{relatorioDocenteDisciplinasMBean.departamentoCombo}" />
						</h:selectOneMenu>	
					</td>
				</tr>
				<tr>
					<th>P�lo:</th>
					<td>
						<h:selectOneMenu value="#{relatorioDocenteDisciplinasMBean.polo.id}" id="selectPolo">
							<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ relatorioDocenteDisciplinasMBean.poloCombo }"/>
						</h:selectOneMenu>	
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Gerar Relat�rio" action="#{relatorioDocenteDisciplinasMBean.gerar}" /> 
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioDocenteDisciplinasMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div align="center">
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>