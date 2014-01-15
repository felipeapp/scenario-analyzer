<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<ufrn:keepAlive tempo="5"/>
<script>	
	var J = jQuery.noConflict();

	var selSubArea;
	var selEspecialidade;
	var area;

	function postArea() {
		J('#area').val(${projetoPesquisaForm.area.id});
		J('#subArea').val( ${projetoPesquisaForm.subarea.id} );
		J('#especialidade').val( ${projetoPesquisaForm.especialidade.id} );
		if (${projetoPesquisaForm.subarea.id} != "") {
			selSubArea.execute();
		}
	}

	function postSubArea() {
		J('#subArea').val( ${projetoPesquisaForm.subarea.id} );
		J('#especialidade').val( ${projetoPesquisaForm.especialidade.id} );
		if (${projetoPesquisaForm.especialidade.id} != "") {
			selEspecialidade.execute();
		}
	}

	function postEspecialidade() {
		J('#especialidade').val( ${projetoPesquisaForm.especialidade.id} );
	}

	function showIsolado(){
		var elementos;
		elementos = document.getElementsByClassName('isolado');
		elementos.each(
			function(el) { el.show(); }
		);
		elementos = document.getElementsByClassName('nIsolado');
		elementos.each(
			function(el) { el.hide(); }
		);
	}

	function showNIsolado(){
		var elementos;
		elementos = document.getElementsByClassName('isolado');
		elementos.each(
			function(el) { el.hide(); }
		);
		elementos = document.getElementsByClassName('nIsolado');
		elementos.each(
			function(el) { el.show(); }
		);
	}

	function checaIsolado(){
		if( !document.getElementById('nIsolado').checked ){
			showIsolado();
		} else{
			showNIsolado();
		}
	}
</script>

<h2> <ufrn:steps/> </h2>

<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa"
	method="post"
	enctype="multipart/form-data"
	focus="obj.titulo">

	<div class="descricaoOperacao">
		<p>
			<strong>Bem-vindo ao cadastro de Projetos de Pesquisa.</strong>
		</p>
		<p>
			Durante as próximas telas você deverá informar os dados referentes ao seu projeto de pesquisa, que será submetido à
			<em>Pró-Reitoria de Pesquisa</em> para a devida avaliação.
		</p>
		<p>
			A seguir serão apresentadas, além desta página inicial, telas com campos reservados para as diferentes partes que compõem um projeto:
			a descrição detalhada, os colaboradores, os financiamentos externos (nos casos em que se aplicarem) e
			a definição de um cronograma de atividades.
		</p>
		<p>
			Após informados os dados iniciais, será possível gravar o projeto a qualquer momento.  <em>Mas atenção: somente serão analisados
			pela Pró-Reitoria de Pesquisa os projetos que tiverem sido completamente preenchidos e confirmados na tela de resumo.</em>
		</p>
		<p>
			Ao final do preenchimento dos formulários, será disponibilizada uma tela de resumo com todos os dados informados para que seja
			realizada a confirmação antes da submissão.
		</p>
	</div>

    <table class="formulario" align="center" width="95%" cellpadding="4" cellspacing="2">
    <caption class="listagem">Informe os dados iniciais do projeto</caption>

	<c:if test="${not empty projetoPesquisaForm.obj.codigo.prefixo }">
	<tr>
		<th> Código do Projeto: </th>
		<td>
			${ projetoPesquisaForm.obj.codigo }
		</td>
	</tr>
	</c:if>
	<tr>
		<th><b> Tipo do Projeto: </b> </th>
		<td>
			${ projetoPesquisaForm.obj.interno ? "INTERNO" : "EXTERNO" }
		</td>
	<tr>
	<tr>
		<th width="25%" class="required">Título:</th>
		<td>
			<c:choose>
				<c:when test="${not projetoPesquisaForm.anexoProjetoBase}">
					<html:textarea property="obj.titulo" rows="2" style="width: 90%" readonly="${projetoPesquisaForm.anexoProjetoBase}" />
				</c:when>
				<c:otherwise>
					<strong>${projetoPesquisaForm.obj.titulo}</strong>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	<tr>
		<th> <b>Unidade:</b> </th>
		<td>
			${ projetoPesquisaForm.obj.unidade }
		</td>
	</tr>
	<tr>
		<th class="required"> Centro: </th>
		<td>
			<c:set var="centros" value="${projetoPesquisaForm.referenceData.centros }" />
			<html:select property="obj.centro.id" style="width:90%">
	        	<html:option value="-1"> -- SELECIONE UMA OPÇÃO --  </html:option>
		        <html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
	        </html:select>
		</td>
	<tr>

	<c:choose>
		<c:when test="${ projetoPesquisaForm.obj.interno && projetoPesquisaForm.obj.edital != null}">
			<c:if test="${!projetoPesquisaForm.segundaChamada}">
			<tr>
				<th class="required">Edital de Pesquisa:</th>
				<td>
					<c:set var="editaisAbertos" value="${projetoPesquisaForm.referenceData.editaisAbertos }" />
					<html:select property="obj.edital.id" style="width:90%" 
						onchange="document.getElementById('dispatch').value = 'carregarDatas'; submit();">
						<html:option value="-1"> -- SELECIONE UMA OPÇÃO --  </html:option>
				        <html:options collection="editaisAbertos" property="id" labelProperty="descricao" />
			        </html:select>
				</td>
			</tr>
			
			<c:if test="${ projetoPesquisaForm.exibirAnos }">
				<tr>
					<td colspan="2">
						<div class="descricaoOperacao">
							<p>
								Este edital permite a submissão de projetos com duração máxima até 
									<ufrn:format type="data" valor="${projetoPesquisaForm.obj.edital.fimExecucaoProjetos}"></ufrn:format>
									. O período de execução escolhido refletirá no cronograma a ser preenchido posteriormente. 
									Caso escolha um período superior a 1(um) ano, o projeto será renovado automaticamente pelo sistema 
									a cada ano.
							</p>
						</div>	
					</td>	
				</tr>	
				<tr>			
					<th>Período Execução:</th>
					<td>
						de: 
							<ufrn:format type="data" valor="${projetoPesquisaForm.obj.edital.inicioExecucaoProjetos}"></ufrn:format>
						até:
						<c:set var="anos" value="${projetoPesquisaForm.referenceData.anos}" />
						<html:select property="obj.tempoEmAnoProjeto" style="width:15%">
					        <html:options collection="anos" property="qntAnos" labelProperty="dataFormatada" />
				        </html:select>
					</td>
				</tr>
			</c:if>
			</c:if>
		</c:when>
		<c:otherwise>
		<tr>
			<th class="required">Período do Projeto:</th>
			<td>
				<ufrn:calendar property="dataInicio"/> a <ufrn:calendar property="dataFim" />
			</td>
		</tr>
		</c:otherwise>
	</c:choose>

	<tr>
		<th class="required"> Palavras-Chave: </th>
		<td>
		 	<html:text property="obj.palavrasChave" maxlength="250" style="width: 90%" />
		</td>
	</tr>
	<tr>
		<th class="required"> E-mail:</th>
		<td>
		 <html:text property="obj.email" maxlength="150" style="width: 90%" />
		</td>
	</tr>
	<c:if test="${not projetoPesquisaForm.obj.interno}">
		<tr>
			<th class="required"> Categoria do projeto: </th>
			<td>
				<html:select property="obj.categoria.id" style="width:90%">
					<html:option value="-1">  -- SELECIONE UMA CATEGORIA --  </html:option>
					<html:options collection="categorias" property="id" labelProperty="denominacao" />
				</html:select>
			</td>
		</tr>
	</c:if>
	<tr>
		<td colspan="2" class="subFormulario"> Área de Conhecimento </td>
	</tr>
	<tr>
		<th class="required">
		Grande Área:
		</th>
		<td>
			<c:set var="grandeAreasCnpq" value="${projetoPesquisaForm.referenceData.grandeAreasCnpq }" />
			<html:select property="grandeArea.id" styleId="grandeArea" style="width:70%" disabled="${projetoPesquisaForm.anexoProjetoBase}">
	        	<html:option value=""> -- SELECIONE UMA GRANDE ÁREA DE CONHECIMENTO --  </html:option>
		        <html:options collection="grandeAreasCnpq" property="id" labelProperty="nome" />
	        </html:select>
		</td>
	</tr>

	<tr>
		<th class="required">
		Área:
		</th>
		<td>
			<html:select property="area.id" styleId="area" style="width:70%">
	        	<html:option value=""> -- SELECIONE ANTES UMA GRANDE ÁREA --  </html:option>
	        </html:select>
		</td>
	</tr>

	<tr>
		<th>
		Subárea:
		</th>
		<td>
			<html:select property="subarea.id" styleId="subArea" style="width:70%">
	        	<html:option value=""> -- SELECIONE ANTES UMA ÁREA --  </html:option>
	        </html:select>
		</td>
	</tr>

	<tr>
		<th>
		Especialidade:
		</th>
		<td>
			<html:select property="especialidade.id" styleId="especialidade" style="width:70%">
	        <html:option value=""> -- SELECIONE ANTES UMA SUB-ÁREA --  </html:option>
	        </html:select>
		</td>
	</tr>

	<tr>
		<td colspan="2" class="subFormulario"> Grupo e Linhas de Pesquisa </td>
	</tr>

	<tr>
		<td colspan="2" align="center"> <strong>Este projeto está vinculado a algum grupo de pesquisa?</strong>
	       	<html:radio property="isolado" value="false" onclick="showNIsolado()" styleId="nIsolado" styleClass="noborder" />
	       	<label for="nIsolado"> Sim </label>
	        <html:radio property="isolado" value="true" onclick="showIsolado()" styleId="isolado" styleClass="noborder" />
	        <label for="isolado"> Não </label>
		</td>
	</tr>

	<tr class="nIsolado">
		<th class="required">Grupo de Pesquisa:</th>
		<td>
			<c:set var="gruposPesquisa" value="${ projetoPesquisaForm.referenceData.gruposPesquisa }" />
			<html:select property="obj.linhaPesquisa.grupoPesquisa.id" styleId="grupoPesquisa" style="width: 90%" >
	        	<html:option value=""> -- SELECIONE UM GRUPO DE PESQUISA -- </html:option>
	        	<html:options collection="gruposPesquisa" property="id" labelProperty="nomeCompacto" />
	        </html:select>
		</td>
	</tr>

	<tr>
		<th class="required">Linha de Pesquisa:</th>
		<td>
			<html:hidden property="obj.linhaPesquisa.id" styleId="idLinhaPesquisa" />
			<html:text property="obj.linhaPesquisa.nome" style="width: 90%" styleId="nomeLinhaPesquisa" />
			<span id="indicatorLinhaPesquisa" style="display:none; "> <img src="/sigaa/img/indicator.gif" width="14" /> </span>

			<ajax:autocomplete
				baseUrl="/sigaa/ajaxLinhaPesquisa"
				source="nomeLinhaPesquisa"
				target="idLinhaPesquisa"
				className="autocomplete"
				parameters="grupo={grupoPesquisa}"
				minimumCharacters="3"
				indicator="indicatorLinhaPesquisa"
				parser="new ResponseXmlToHtmlListParser()" />
		</td>
	</tr>

	<c:if test="${ !projetoPesquisaForm.obj.interno}">
		<tr>
			<td colspan="2" class="subFormulario"> Definição da Propriedade Intelectual </td>
		</tr>
		<tr>
			<td colspan="2" style="text-align: center;">
				<p style="padding: 4px 0; color: #555;">
					Informe o tratamento da produção intelectual deste projeto caso esta tenha sido definida.
				</p>
				<ufrn:textarea property="obj.definicaoPropriedadeIntelectual" rows="3" maxlength="400" style="width: 90%" />
			</td>
		</tr>
	</c:if>

	<tfoot>
		<tr>
		<td colspan="2">
			<html:button dispatch="cancelar" value="Cancelar" cancelar="true" />
			<html:button dispatch="descricao" value="Avançar >>" />
		</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</html:form>

<script>
	J(document).ready(function () {
		checaIsolado();
		postArea();
		postSubArea();
		postEspecialidade();
</script>

	<ajax:select
	  baseUrl="${pageContext.request.contextPath}/ajaxProjetoPesquisa?tipo=area&tipoArea=area"
	  source="grandeArea"
	  target="area"
	  parameters="id={grandeArea}"
      postFunction="postArea"
	  executeOnLoad="true"
	 />

	<ajax:select
	  baseUrl="${pageContext.request.contextPath}/ajaxProjetoPesquisa?tipo=area&tipoArea=subarea"
	  var="selSubArea"
	  source="area"
	  target="subArea"
	  parameters="id={area}"
	  postFunction="postSubArea"
	  executeOnLoad="true"/>

	<ajax:select
	  baseUrl="${pageContext.request.contextPath}/ajaxProjetoPesquisa?tipo=area&tipoArea=especialidade"
	  var="selEspecialidade"
	  source="subArea"
	  target="especialidade"
	  parameters="id={subArea}"
	  postFunction="postEspecialidade"
	  executeOnLoad="true"/>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>