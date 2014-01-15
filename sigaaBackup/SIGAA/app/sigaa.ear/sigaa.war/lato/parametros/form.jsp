<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.dominio.ModalidadeEducacao"%>

<f:view>
<h2><ufrn:subSistema /> &gt; Parâmetros Proposta Curso Lato Sensu</h2>

<div class="descricaoOperacao" style="width: 80%;">
	Caro usuário, 
	<ul>
		<li>
			<b>Docentes Internos</b>: Qual a percentagem mínima de docentes internos que deve está presente na
			 proposta de curso Lato Sensu.
		</li>
		<br />
		<li>
			<b>Carga Horária Máxima</b>: Carga horária máxima que um docente podem assumir dentro da proposta de curso Lato Sensu, 
			em relação a carga horária do curso.    
		</li> 
		<br />
		<li>
			<b>CH Docentes Internos</b>: Somatório da carga horária de todos os docentes internos deve ser no mínimo a 
			percentagem de horas informada do curso.   
		</li> 
		<br />
		<li>
			<b>CH Docentes Externos</b>: Somatório da carga horária de todos os docentes externos deve ser no máximo a 
			percentagem horas informada do curso.   
		</li> 
	</ul>
</div>

<h:form id="form">
  <table class=formulario width="100%" border="1">
   <caption class="listagem">Parâmetros Proposta Curso Lato Sensu</caption>
	 <tr>
	  <td colspan="2">
	    <table class="subFormulario" width="100%">
		 <caption>Corpo Docente</caption>
		 	<h:inputHidden value="#{parametrosPropostaCursoLatoMBean.obj.id}"/>
			<tr>
				<th style="width: 250px;">Docentes Internos:</th>
				<td><h:inputText value="#{parametrosPropostaCursoLatoMBean.obj.porcentagemServidores}" maxlength="3" size="4" id="porcentagemDocenteInterno" />%</td>
			</tr>
			<tr>
				<th>Carga Horária Máxima:</th>
				<td>
					<h:inputText value="#{parametrosPropostaCursoLatoMBean.obj.chTotalDocenteCurso}" maxlength="3" size="4" id="cargaHorariaMaxima" />% do curso
					<ufrn:help>Porcentagem máxima da carga horária curso para cada docente.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>CH Docentes Internos:</th>
				<td>
					<h:inputText value="#{parametrosPropostaCursoLatoMBean.obj.porcentagemMinimaDocentesInternos}" maxlength="3" size="4" id="docentesInterno" />%
					<ufrn:help>Carga horária do curso destinada aos docentes Internos</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>CH Docentes Externos:</th>
				<td>
					<h:inputText value="#{parametrosPropostaCursoLatoMBean.obj.porcentagemMaximaDocentesExternos}" maxlength="3" size="4" id="docentesExternos" />%
					<ufrn:help>Carga horária do curso destinada aos docentes Externos</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Vagas Servidores:</th>
				<td>
					<h:inputText value="#{parametrosPropostaCursoLatoMBean.obj.porcentagemVagasServidores}" maxlength="3" size="4" id="vagasServidores" />%
					<ufrn:help>Vagas do curso destinada aos Servidores da Instituição</ufrn:help>
				</td>
			</tr>
			<tr>
				<th>Curriculo Lattes:</th>
				<td>
					<h:selectBooleanCheckbox value="#{ parametrosPropostaCursoLatoMBean.obj.curriculoLattesObrigatorio }" />
					<ufrn:help>É necessário que o docente possua o Currículo Lattes cadastrado?</ufrn:help>
				</td>
			</tr>
	    </table>
   	   </td>
	  </tr>
	  <tr>
  		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="Cadastrar" action="#{parametrosPropostaCursoLatoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" action="#{parametrosPropostaCursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
				</td>
		   </tr>
		</tfoot>
   </table>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>