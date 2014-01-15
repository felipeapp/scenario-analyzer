<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<h2 class="tituloPagina">
	<ufrn:steps/>
</h2>

<script language="javascript">
var selSubArea;
var selEspecialidade;

function postArea() {
	$('area').value = ${cursoLatoForm.area.id};
	if (${cursoLatoForm.subarea.id} != "") {
		selSubArea.execute();
	}
};

function postSubArea() {
	$('subArea').value = ${cursoLatoForm.subarea.id};
	if (${cursoLatoForm.especialidade.id} != "") {
		selEspecialidade.execute();
	}
};

function postEspecialidade() {
	$('especialidade').value = ${cursoLatoForm.especialidade.id};
};

function certificado(bool) {
	$('setorResponsavel').disabled = !bool;
};

function cursoPago(bool) {
	$('entidadeFinanciadora').disabled = bool;
	$('valorCurso').disabled = !bool;
	$('qtdMensalidades').disabled = !bool;
	$('percentualInstituicao').disabled = !bool;
	$('percentualCoordenador').disabled = !bool;
};

function mostraProcesso(obj) {
	if (obj) {
		Element.show('nr_processo');
	} else {
		Element.hide('nr_processo');
	}

};
</script>

<input type="hidden" id="linkAjuda" value="/manuais/lato/Proposta/1_dados_basicos.htm">

<html:form action="/ensino/latosensu/criarCurso" method="post" focus="obj.nome">
<table class="formulario" align="center" width="100%">
<caption>Criação de Proposta de Curso Lato Sensu</caption>
<tr>
<td colspan="2">
	<table class="subFormulario" width="100%">
	<caption>Dados Básicos do Curso</caption>
	<tr>
    	<th class="required">Nome:</th>
    	<td colspan="4">
    		<html:text  property="obj.nome" style="width:80%" maxlength="80" onkeyup="CAPS(this)" />
    	</td>
    </tr>
    <tr>
    	<th class="required">Unidade Responsável:</th>
    	<td colspan="4">
    		<html:select property="obj.unidade.id" style="width:80%">
            	<html:option value=""> -- SELECIONE -- </html:option>
            	<html:options collection="programasDeptos" property="id" labelProperty="nome"/>
           	</html:select>
    		<ufrn:help img="/img/ajuda.gif">Departamento ou Programa de Pós-graduação que está oferecendo o curso.</ufrn:help>
    	</td>
    </tr>
    <tr>
    	<th class="required">Tipo do Curso:</th>
    	<td colspan="4">
          <html:select property="obj.tipoCursoLato.id" style="width:40%">
            <html:option value=""> -- SELECIONE -- </html:option>
            <html:options collection="tipoCursos" property="id" labelProperty="descricao"/>
           </html:select>
    	</td>
    </tr>
    <tr>
    	<th class="required">Modalidade da Educação:</th>
    	<td colspan="4">
          <html:select property="obj.modalidadeEducacao.id" style="width:40%">
           <html:option value=""> -- SELECIONE -- </html:option>
           <html:options collection="modalidadesEducacoes" property="id" labelProperty="descricao"/>
          </html:select>
    	</td>
    </tr>
    <tr>
    	<th class="required">Carga Horária:</th>
    	<td colspan="4">
    		<html:text  property="obj.cargaHoraria" size="4" maxlength="4" onkeyup="formatarInteiro(this)" />
    	</td>
    </tr>
    <tr>
    	<th class="required">Número de Vagas:</th>
    	<td colspan="4">
			<html:text property="obj.numeroVagas" size="4" onkeyup="formatarInteiro(this)" />
    	</td>
    </tr>
    <tr>
		<th class="required">Grande Área:</th>
		<td colspan="4">
			<html:select property="grandeArea.id" styleId="grandeArea" style="width:40%">
	        <html:option value=""> -- SELECIONE UMA OPÇÃO --  </html:option>
	        <html:options collection="grandeAreasCnpq" property="id" labelProperty="nome" />
	        </html:select>
		</td>
	</tr>
	<tr>
		<th class="required">Área:</th>
		<td colspan="4">
			<html:select property="area.id" styleId="area" style="width:40%">
	        	<html:option value=""> -- SELECIONE UMA OPÇÃO --  </html:option>
	        </html:select>
		</td>
	</tr>

	<ajax:select
	  baseUrl="${pageContext.request.contextPath}/ajaxProjetoPesquisa?tipo=area&tipoArea=area"
	  source="grandeArea"
	  target="area"
	  parameters="id={grandeArea}"
      postFunction="postArea"
	  executeOnLoad="true" />

	<tr>
		<th>Sub-Área:</th>
		<td colspan="4">
			<html:select property="subarea.id" styleId="subArea" style="width:40%">
	        	<html:option value=""> -- SELECIONE UMA OPÇÃO --  </html:option>
	        </html:select>
		</td>
	</tr>

	<ajax:select
	  baseUrl="${pageContext.request.contextPath}/ajaxProjetoPesquisa?tipo=area&tipoArea=subarea"
	  var="selSubArea"
	  source="area"
	  target="subArea"
	  parameters="id={area}"
	  postFunction="postSubArea" />

	<tr>
		<th>Especialidade:</th>
		<td colspan="4">
			<html:select property="especialidade.id" styleId="especialidade" style="width:40%">
	        <html:option value=""> -- SELECIONE UMA OPÇÃO --  </html:option>
	        </html:select>
		</td>
	</tr>

	<ajax:select
	  baseUrl="${pageContext.request.contextPath}/ajaxProjetoPesquisa?tipo=area&tipoArea=especialidade"
	  var="selEspecialidade"
	  source="subArea"
	  target="especialidade"
	  parameters="id={subArea}"
	  postFunction="postEspecialidade" />

	<tr>
    	<th class="required">Tipo do Trabalho de Conclusão:</th>
    	<td colspan="4">
			 <html:select property="obj.tipoTrabalhoConclusao.id" style="width:40%">
              <html:option value=""> -- SELECIONE -- </html:option>
              <html:options collection="tiposTrabalhoConclusao" property="id" labelProperty="descricao"/>
             </html:select>
    	</td>
    </tr>
    <tr>
    	<th>Banca Examinadora: 	</th>
    	<td colspan="4">
    		<html:radio property="obj.bancaExaminadora" styleClass="noborder" value="true" />Sim
   			<html:radio property="obj.bancaExaminadora" styleClass="noborder" value="false" />Não
    	</td>
    </tr>
    <tr>
    	<th>Emissão de Certificado: </th>
    	<td>
    		<html:radio property="obj.certificado" styleClass="noborder" value="true" onclick="javascript:certificado(true);" />Sim
   			<html:radio property="obj.certificado" styleClass="noborder" value="false" onclick="javascript:certificado(false);" />Não
    	</td>
    	<th>Setor Responsável pela Emissão:</th>
    	<td>
    		<html:text property="obj.setorResponsavelCertificado" size="30" maxlength="50" styleId="setorResponsavel" />
    	</td>
    </tr>
    <tr>
    	<th>Curso Pago:	</th>
    	<td>
    		<html:radio property="obj.cursoPago" styleClass="noborder" value="true" onclick="javascript:cursoPago(true);" />Sim
   			<html:radio property="obj.cursoPago" styleClass="noborder" value="false" onclick="javascript:cursoPago(false);" />Não
    	</td>
    	<th>Entidade Financiadora: </th>
    	<td>
    		<html:text property="obj.entidadeFinanciadora" size="30" maxlength="50" styleId="entidadeFinanciadora" />
    	</td>
    </tr>
    <tr>
    	<th>Valor da Mensalidade:</th>
    	<td>
    		<html:text property="valor" size="10" onkeydown="return(formataValor(this, event, 2))" styleId="valorCurso"/>
    	</td>
    	<th>Quantidade de Mensalidades:</th>
    	<td>
    		<html:text property="obj.qtdMensalidades" size="2" styleId="qtdMensalidades"/>
    	</td>
    </tr>
    <tr>
    	<th>Divisão dos Recursos:</th>
    	<td colspan="4">
    		<table>
    		<tr>
    			<th>Instituição (%):</th>
    			<td>
    				<html:text property="proposta.percentualInstituicao" size="4" styleId="percentualInstituicao"/>
    			</td>
    			<th>Coordenador (%):</th>
		    	<td>
					<html:text property="proposta.percentualCoordenador" size="4" styleId="percentualCoordenador"/>
		    	</td>
    		</tr>
    		</table>
    	</td>
    </tr>
    <tr>
    	<th class="required">Período do Curso:</th>
    	<td colspan="4">
    		<table>
    		<tr>
    			<th></th>
    			<td>
    				<ufrn:calendar property="dataInicio" />
				</td>
				<th>&nbsp;a</th>
		    	<td>
					<ufrn:calendar property="dataFim" /> 
		    	</td>
    		</tr>
    		</table>
    	</td>
    </tr>
    <tr>
    	<th>Curso em Andamento:	</th>
    	<td colspan="4">
    		<html:radio property="andamento" styleClass="noborder" value="true" onclick="javascript:mostraProcesso(true);" />Sim
   			<html:radio property="andamento" styleClass="noborder" value="false" onclick="javascript:mostraProcesso(false);" />Não
    	</td>
    </tr>
    <tr id="nr_processo">
			<th class="required">Número/Ano do Processo:</th>
			<td colspan="4">
				23077.
				<html:text property="obj.propostaCurso.numeroProcesso" size="6" maxlength="6"/>/
				<html:text property="obj.propostaCurso.anoProcesso" size="4" maxlength="4"/>-XX
				(ex.: 23077.001234/2003-98)
			</td>
		</tr>
    </table>
</td>
</tr>
<tr>
<td colspan="2">
   		<table class="subformulario" width="100%">
   		<caption>Público Alvo</caption>
    		<tr>
    		<td colspan="4"><b>Categorias:</b></td>
    		</tr>
    		<c:forEach items="${gruposPublicoAlvo}" var="grupo">
    		<tr>
    			<td>
    			${grupo.descricao}
	    		<c:forEach items="${grupo.tipos}" var="tipoPublicoAlvo" varStatus="pos">
	    				<br/>
		    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }"
								<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
									<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
								</c:forEach>
						/>
						${ tipoPublicoAlvo.descricao }
				</c:forEach>
				</td>
			</tr>
    		</c:forEach>
    		<tr>
		    	<td valign="top" width="100">
		    		Sexo:
		    		<c:forEach items="${tiposPublicoAlvoCurso}" var="tipoPublicoAlvo" varStatus="pos">
		    			<c:if test="${ tipoPublicoAlvo.grupo.id == 1 }" >
		    				<br/>
			    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
										<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ tipoPublicoAlvo.descricao }
						</c:if>
					</c:forEach>
					<br/><br/>
					Idade:
					<c:forEach items="${tiposPublicoAlvoCurso}" var="tipoPublicoAlvo" varStatus="pos">
		    			<c:if test="${ tipoPublicoAlvo.grupo.id == 2 }" >
		    				<br/>
			    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
										<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ tipoPublicoAlvo.descricao }
						</c:if>
					</c:forEach>
		    	</td>

		    	<td valign="top" width="110">
		    		Nacionalidade:
		    		<c:forEach items="${tiposPublicoAlvoCurso}" var="tipoPublicoAlvo" varStatus="pos">
		    			<c:if test="${ tipoPublicoAlvo.grupo.id == 3 }" >
		    				<br/>
			    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
										<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ tipoPublicoAlvo.descricao }
						</c:if>
					</c:forEach>
					<br/><br/>
					Renda:
					<c:forEach items="${tiposPublicoAlvoCurso}" var="tipoPublicoAlvo" varStatus="pos">
		    			<c:if test="${ tipoPublicoAlvo.grupo.id == 4 }" >
		    				<br/>
			    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
										<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ tipoPublicoAlvo.descricao }
						</c:if>
					</c:forEach>
		    	</td>

		    	<td valign="top">
		    		Escolaridade:
		    		<c:forEach items="${tiposPublicoAlvoCurso}" var="tipoPublicoAlvo" varStatus="pos">
		    			<c:if test="${ tipoPublicoAlvo.grupo.id == 5 }" >
		    				<br/>
			    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
										<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ tipoPublicoAlvo.descricao }
						</c:if>
					</c:forEach>
		    	</td>
		    	<td valign="top">
		    		Profissão:
		    		<c:forEach items="${tiposPublicoAlvoCurso}" var="tipoPublicoAlvo" varStatus="pos">
		    			<c:if test="${ tipoPublicoAlvo.grupo.id == 6 }" >
		    				<br/>
			    			<input type="checkbox" name="tiposPublicoAlvo" value="${ tipoPublicoAlvo.id }" class="noborder"
									<c:forEach items="${cursoLatoForm.obj.publicosAlvoCurso}" var="latoPublicoBean">
										<c:if test="${latoPublicoBean.tipoPublicoAlvo.id == tipoPublicoAlvo.id}"> checked="checked"</c:if>
									</c:forEach>
							/>
							${ tipoPublicoAlvo.descricao }
						</c:if>
					</c:forEach>
		    	</td>
		    </tr>
    		</table>
    	</td>
    </tr>
	<tfoot>
		<tr>
		<td colspan="2">
    		<html:button dispatch="gravar" value="Gravar"/>
    		<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
    		<html:button dispatch="proposta" value="Avançar >>" />
		</td>
		</tr>
	</tfoot>
</table>

<script>
	certificado(${cursoLatoForm.obj.certificado});
	cursoPago(${cursoLatoForm.obj.cursoPago});
	mostraProcesso(${cursoLatoForm.andamento});
</script>

</html:form>

<br>
<center><html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span></center>
<br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>