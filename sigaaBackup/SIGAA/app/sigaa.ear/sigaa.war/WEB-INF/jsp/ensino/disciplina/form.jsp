	<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<!--  Scripts do YAHOO -->
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/tabs.css">
<link rel="stylesheet" type="text/css" href="/shared/javascript/yui/tabview/assets/border_tabs.css">
<script type="text/javascript" src="/shared/javascript/yui/tabview-min.js"></script>

<c:if test="${disciplinaForm.obj.id > 0 }">
	<style>
		table tbody tr th {font-weight: bold;}
	</style>
</c:if>

<script type="text/javascript">
var criarAbas = function() {
    var tabView = new YAHOO.widget.TabView('tabs-componenteCurricular');
};
criarAbas();
</script>

<h2 class="tituloPagina"><html:link action="/ensino/cadastroDisciplina?dispatch=cancelar">
	<ufrn:subSistema semLink="true" />
</html:link> <ufrn:subSistema teste="infantil">
	&gt; Nível Infantil
	</ufrn:subSistema> <ufrn:subSistema teste="not infantil">
	&gt; Disciplinas
	</ufrn:subSistema></h2>

<html:form action="/ensino/cadastroDisciplina" method="post"
	focus="obj.detalhes.nome" styleId="form">

	<html:hidden property="obj.id" />
	<html:hidden property="obj.unidade.id" value="${ sessionScope.usuario.unidade.id }" />

	<table class="formulario" width="95%" border="1">
		<ufrn:subSistema teste="infantil">
			<caption>Dados do Nível Infantil</caption>
		</ufrn:subSistema>
		<ufrn:subSistema teste="not infantil">
			<caption>Dados da Disciplina</caption>
		</ufrn:subSistema>

		<tbody>
			<tr>
				<td>
				<table class="subFormulario" width="100%">
					<caption>Dados Gerais</caption>
					<c:if test="${disciplinaForm.obj.id > 0 }">
					<tr>
						<th>Código:</th>
						<td>${disciplinaForm.obj.codigo }</td>
					</tr>
					</c:if>

					<tr>
						<th width="15%">Nome: &nbsp;<html:img page="/img/required.gif" /></th>
						<td><html:text property="obj.detalhes.nome" maxlength="150" size="85" onkeyup="CAPS(this)" styleId="detalhesNome"  />
						</td>
					</tr>

					<tr>
						<th>Carga Horária:&nbsp;<html:img page="/img/required.gif" /></th>
						<td><ufrn:subSistema teste="infantil">
							<html:text property="obj.detalhes.chAula" size="6" maxlength="6" onkeyup="return formatarInteiro(this);" styleId="detalhesChAulaInfantil"/>
						</ufrn:subSistema> <ufrn:subSistema teste="not infantil">
    					&nbsp;&nbsp;&nbsp;<i>Aula:</i>
							<html:text property="obj.detalhes.chAula" size="6" maxlength="6" onkeyup="return formatarInteiro(this);" styleId="detalhesChAula"/>
           				 &nbsp;&nbsp;&nbsp;<i>Laboratório:</i>
							<html:text property="obj.detalhes.chLaboratorio" size="6" maxlength="6" onkeyup="return formatarInteiro(this);" styleId="detalhesChLaboratorio"/>
           				 &nbsp;&nbsp;&nbsp;<i>Estágio:</i>
							<html:text property="obj.detalhes.chEstagio" size="6" maxlength="6" onkeyup="return formatarInteiro(this);" styleId="detalhesChEstagio"/>
							</span>
						</ufrn:subSistema></td>
					</tr>
					<tr>
						<th>Pré-requisitos:</th>
						<td><html:text property="obj.preRequisito" size="85" onkeyup="CAPS(this)" styleId="preRequisito" /></td>
					</tr>
					<tr>
						<th>Có-requisitos:</th>
						<td><html:text property="obj.coRequisito" size="85" onkeyup="CAPS(this)" styleId="coRequisito" /></td>
					</tr>
					<tr>
						<th>Equivalência:</th>
						<td><html:text property="obj.equivalencia" size="85" onkeyup="CAPS(this)" styleId="equivalencia"/></td>
					</tr>
					<tr>
						<th>Quantidade de Avaliações:</th>
						<td><html:select property="obj.numUnidades" styleId="numUnidades">
							<c:forEach items="${disciplinaForm.maxAvaliacoes}" var="av">
								<html:option value="${av }">${av }</html:option>
							</c:forEach>
						</html:select></td>
					</tr>
					<tr>
						<th valign="top">Ementa:&nbsp;<html:img page="/img/required.gif" /></th>
						<td><ufrn:textarea property="obj.detalhes.ementa" styleId="detalhesEmenta" cols="80" rows="6" />
						</td>
					</tr>
					<tr>
						<th valign="top">Ativo:</th>
						<td>
						<html:radio property="obj.ativo" value="true" styleId="sim" /> <label for="sim">Sim</label> <br>
						<html:radio property="obj.ativo" value="false" styleId="nao" /> <label for="nao">Não</label>
						</td>
					</tr>
					<tr>
						<th valign="top" style="width: 20%;">Matriculável "On-Line":</th>
						<td>
							<html:radio property="obj.matriculavel" value="true" styleId="sim_mat" /> <label for="sim_mat">Sim</label> <br>
							<html:radio property="obj.matriculavel" value="false" styleId="nao_mat" /> <label for="nao_mat">Não</label>
							<ufrn:help img="/img/ajuda.gif">As disciplinas marcadas como "Matriculável On-Line"
							estarão disponíveis para matrícula on-line dos alunos. Caso contrário, as matrículas
							nas turmas dessas disciplinas precisam ser feitas na coordenação do curso.</ufrn:help>
						</td>
					</tr>
				</table>
				</td>
			</tr>

			<tr>
				<td>
				<table class="subFormulario" width="100%">
					<caption>Dados do Programa</caption>
					<tr>
						<td>
						<div id="tabs-componenteCurricular" class="yui-navset">
						<ul class="yui-nav">
							<li><a href="#objetivos"><em>Objetivos</em></a></li>
							<li><a href="#conteudo"><em>Conteúdo</em></a></li>
							<li><a href="#competencias"><em>Competências</em></a></li>
							<li><a href="#metodologia"><em>Metodologia</em></a></li>
							<li><a href="#avaliacao"><em>Avaliação</em></a></li>
							<li><a href="#referencias"><em>Referências</em></a></li>
						</ul>

						<div class="yui-content">
						<div id="objetivos"><ufrn:textarea property="obj.programa.objetivos"  styleId="programaObjetivos"
							cols="100" rows="6" /></div>
						<div id="conteudo"><ufrn:textarea property="obj.programa.conteudo" styleId="programaConteudo"
							cols="100" rows="6" /></div>
						<div id="competencias"><ufrn:textarea property="obj.programa.competenciasHabilidades" styleId="programaCompetenciasHabilidades"
							 cols="100" rows="6" /></div>
						<div id="metodologia"><ufrn:textarea property="obj.programa.metodologia" styleId="programaMetodologia"
							 cols="100" rows="6" /></div>
						<div id="avaliacao"><ufrn:textarea property="obj.programa.procedimentosAvaliacao" styleId="programaProcedimentosAvaliacao"
							 cols="100" rows="6" /></div>
						<div id="referencias"><ufrn:textarea property="obj.programa.referencias" styleId="programaReferencias"
							 cols="100" rows="6" /></div>
						</div>
						</div>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="2">
					<html:button dispatch="persist" value="Confirmar"/>
					<input type="hidden" name="page" id="page" value="${param.page}" />
					<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
				</td>
			</tr>
		</tfoot>

	</table>

</html:form>
<br>
<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
	class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
<br>
</center>

<c:if test="${ param['dispatch'] == 'remove' }">
	<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
