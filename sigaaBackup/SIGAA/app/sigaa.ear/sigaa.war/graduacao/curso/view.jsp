<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:outputText value="#{cursoGrad.create }"></h:outputText>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Cursos</h2>

		<table class="visualizacao" width="99%">
			<caption class="formulario">Dados do Curso</caption>
			<tr>
				<th width="25%">Nome</th>
				<td><h:outputText id="nome" value="#{cursoGrad.obj.descricao}" /> </td>
			</tr>
			<c:if test="${cursoGrad.stricto}">
				<tr>
					<th>Nível</th>
					<td><h:outputText id="nivel" value="#{cursoGrad.obj.nivelDescricao}" /></td>
				</tr>
				<tr>
					<th>Tipo Stricto</th>
					<td><h:outputText id="tipoStricto" value="#{cursoGrad.obj.tipoCursoStricto.descricao}" /></td>
				</tr>
				<tr>
					<th>Titulação:</th>
					<td>
						<h:outputText id="titulacao" value="#{cursoGrad.obj.titulacao}"/>
					</td>
				</tr>
				<tr>
					<th>Código CAPES do Curso</th>
					<td><h:outputText id="codCapes"	value="#{cursoGrad.obj.codigo}" /> </td>
				</tr>
				<tr>
					<th>Código CAPES do Programa</th>
					<td><h:outputText id="codCapesProg"	value="#{cursoGrad.obj.codProgramaCAPES}" /> </td>
				</tr>
			</c:if>
			<c:if test="${!cursoGrad.stricto}">
			<tr>
				<th>Código INEP</th>
				<td>
					<h:outputText id="codigoINEP" value="#{cursoGrad.obj.codigoINEP}"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Município de Andamento do Curso</th>
				<td><h:outputText id="municipio"	value="#{cursoGrad.obj.municipio.nomeUF}" /> </td>
			</tr>
			<tr>
				<th>Área do Curso</th>
				<td><h:outputText id="area"	value="#{cursoGrad.obj.areaCurso.nome}" /> </td>
			</tr>
			<tr>
				<th>Forma de Participação do Aluno</th>
				<td><h:outputText id="modalidade"	value="#{cursoGrad.obj.modalidadeEducacao.descricao}" /> </td>
			</tr>
			<tr>
				<th>Natureza do Curso</th>
				<td><h:outputText id="natureza"	value="#{cursoGrad.obj.naturezaCurso.descricao}" /> </td>
			</tr>
			<tr>
				<th>Tipo de Oferta do Curso</th>
				<td><h:outputText id="tipoOferta"	value="#{cursoGrad.obj.tipoOfertaCurso.descricao}" /> </td>
			</tr>
			<tr>
				<th>Convênio Acadêmico</th>
				<td><h:outputText id="convenio"	value="#{cursoGrad.obj.convenio.descricao}" /> </td>
			</tr>
			<tr>
				<th>Unidade Responsável</th>
				<td><h:outputText id="unidade"	value="#{cursoGrad.obj.unidade.nome}" /> </td>
			</tr>
			<c:if test="${not cursoGrad.stricto}">
				<tr>
					<th>Unidade da Coordenação</th>
					<td><h:outputText id="unidadeCoordenacao"	value="#{cursoGrad.obj.unidadeCoordenacao.nome}" /> </td>
				</tr>
			</c:if>			
		</table>
		
		<br>
		<center>
			<h:form>
				<input type="button" value="<< Selecionar Outro Curso"  onclick="javascript: history.back();" id="voltar" />
				<h:commandButton action="#{cursoGrad.cancelar}" value="Cancelar" onclick="#{confirm}"/>
			</h:form>
		</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
