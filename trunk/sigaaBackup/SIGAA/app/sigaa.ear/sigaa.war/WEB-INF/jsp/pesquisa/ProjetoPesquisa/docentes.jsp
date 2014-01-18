<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.CategoriaMembro"%>

<c:choose>
	<c:when test="${ projetoPesquisaForm.renovacao }">
		<h2> <ufrn:subSistema /> &gt; Renovação de Projetos de Pesquisa &gt; Dados da Renovação &gt; Membros </h2>
	</c:when>
	<c:otherwise>
		<h2> <ufrn:steps/> </h2>
	</c:otherwise>
</c:choose>

<ufrn:keepAlive tempo="5"/>

<style>
	.infoAltRem {
	  width: 95%;
	}

	div.aba {
		border: 0;
	}

</style>

<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post">

	<table class="formulario" style="width: 95%;">
	<caption class="listagem">Informar membros do projeto</caption>
	<tr>
		<td colspan="2">
			<p style="text-align: center; font-style: italic; padding: 5px;">
				Selecione a categoria do membro para realizar a busca de acordo com os critérios específicos
			</p>
			<div id="abas-membro">
				<html:hidden property="membroProjeto.categoriaMembro.id" styleId="categoriaMembro"/>
				<div id="membro-docente" class="aba">
					<table width="100%">
					<tr>
						<th width="25%" class="required">Buscar Docente:</th>
						<td>
							<c:set var="idAjax" value="membroProjeto.servidor.id"/>
							<c:set var="nomeAjax" value="membroProjeto.servidor.pessoa.nome"/>
							<c:set var="somenteInternos" value="true"/>
							<c:set var="buscaInativos" value="true"/>
							<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
						</td>
					</tr>
					</table>
				</div>
				<div id="membro-discente" class="aba">
					<table width="100%">
						<tr>
							<th width="25%">Buscar Discente:</th>
							<td>
								<c:set var="idAjax" value="membroProjeto.discente.id"/>
								<c:set var="nomeAjax" value="membroProjeto.discente.pessoa.nome"/>
								<c:set var="opcoesNivel" value="true"/>
								<c:set var="showResidente" value="true"/>
								<c:set var="ignorarUnidade" value="sim"/>

								<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
							</td>
						</tr>
					</table>
				</div>
				<div id="membro-servidor" class="aba">
					<table width="100%">
						<tr>
							<th width="25%">Servidor:</th>
							<td>
								<c:set var="idAjax" value="servidorTecnico.id"/>
								<c:set var="nomeAjax" value="servidorTecnico.pessoa.nome"/>
								<c:set var="tipo" value="todos"/>
								<%@include file="/WEB-INF/jsp/include/ajax/servidor.jsp" %>
							</td>
						</tr>
					</table>
				</div>
				<div id="membro-externo" class="aba">
					<table width="100%">
						<tr>
							<th class="obrigatorio">CPF:</th>
							<td width="20%">
								<html:text property="cpfExterno" maxlength="14" size="14" onblur="formataCPF(this, event, null)" styleId="cpfExterno"/>
							</td>
							<td width="2%">
								<html:checkbox property="membroProjeto.docenteExterno.pessoa.internacional" styleId="checkEstrangeiro" onclick="verificaEstrangeiro()"/>
							</td>
							<td> <label for="checkEstrangeiro"> ESTRANGEIRO (sem CPF)</label> </td>
						</tr>
						<tr>
							<th width="20%" class="obrigatorio">Nome:</th>
							<td colspan="3">
								<html:text property="membroProjeto.docenteExterno.pessoa.nome" size="80" maxlength="80"/>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Sexo:</th>
							<td colspan="3">
								<html:radio property="membroProjeto.docenteExterno.pessoa.sexo" value="M" styleId="radioMasculino"/>
								<label for="radioMasculino"> Masculino </label>
								<html:radio property="membroProjeto.docenteExterno.pessoa.sexo" value="F" styleId="radioFeminino"/>
								<label for="radioFeminino"> Feminino </label>
							</td>
						</tr>
						<tr>
							<th>Formação:</th>
							<td colspan="3">
								<c:set var="formacoes" value="${ projetoPesquisaForm.referenceData.formacoes }"/>
								<html:select property="membroProjeto.docenteExterno.formacao.id" style="width:95%">
						        	<html:options collection="formacoes" property="id" labelProperty="denominacao" />
						        </html:select>
							</td>
						</tr>
						<tr>
							<th class="obrigatorio">Tipo:</th>
							<td colspan="3">
								<c:set var="tipos" value="${ projetoPesquisaForm.referenceData.tipos }"/>
								<html:select property="membroProjeto.docenteExterno.tipoDocenteExterno.id" style="width:95%">
						        	<html:options collection="tipos" property="id" labelProperty="denominacao" />
						        </html:select>
							</td>
						</tr>
						<tr>
							<th>Instituição de Origem:</th>
							<td colspan="3">
								<c:set var="instituicoes" value="${ projetoPesquisaForm.referenceData.instituicoes }"/>
								<html:select property="membroProjeto.docenteExterno.instituicao.id" style="width:95%">
									<html:option key="-1" value="  NÃO INFORMADA   "/>
						        	<html:options collection="instituicoes" property="id" labelProperty="nomeSigla" />
						        </html:select>
							</td>
						</tr>
					</table>
				</div>
				<div>
			</div>
		</td>
	</tr>
	<tr>
		<th> Função: </th>
		<td>
			<html:radio property="membroProjeto.funcaoMembro.id" value="<%=""+FuncaoMembro.COORDENADOR %>" styleClass="noborder" styleId="radioCoordenador"/>
			<label for="radioCoordenador">COORDENADOR(A)</label>
			<html:radio property="membroProjeto.funcaoMembro.id" value="<%=""+FuncaoMembro.VICE_COORDENADOR %>" styleClass="noborder" styleId="radioVice"/>
			<label for="radioVice">COORDENADOR ADJUNTO(A)</label>
			<html:radio property="membroProjeto.funcaoMembro.id" value="<%=""+FuncaoMembro.COLABORADOR %>" styleClass="noborder" styleId="radioColaborador"/>
			<label for="radioColaborador">COLABORADOR(A)</label>
		</td>
	</tr>			
	<tr>
		<th width="26%" class="obrigatorio"> CH dedicada ao projeto: </th>
		<td>
			<html:text property="membroProjeto.chDedicada" size="4" value="" maxlength="2" onkeyup="formatarInteiro(this);"/> horas semanais
		</td>
	</tr>
	<tfoot>
		<tr>
		<td colspan="2">
			<html:button dispatch="adicionarMembro" value="Adicionar Membro"/>
		</td>
		</tr>

	</tfoot>
	</table>

<html:hidden property="posLista" styleId="posicao"/>
<c:set var="posicao" value="0"/>

<br/>
<div class="infoAltRem">
	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Membro
</div>

<table class="listagem" style="width: 95%;">
<caption class="listagem"> Lista de Membros </caption>
	<c:choose>
		<c:when test="${not empty projetoPesquisaForm.obj.membrosProjeto}">
			<thead>
			<tr>
			    <th style="text-align: center;"> CPF </th>
			    <th> Nome </th>
			    <th> Categoria </th>
			    <th> CH Semanal </th>
			    <th> Função </th>
			    <th> </th>
			</tr>
			</thead>
			<tbody>
		   <c:forEach items="${ projetoPesquisaForm.obj.membrosProjeto}" var="membro" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td  style="text-align: center;">
						<html:hidden property="membro.pessoa.id" value="${membro.pessoa.id}" />
						<ufrn:format type="cpf_cnpj" name="membro" property="pessoa.cpf_cnpj"/>
					</td>
					<td>${ membro.pessoa.nome }</td>
					<td>${ membro.categoriaMembro.descricao }</td>
					<td>
						${ membro.chDedicada != null ? membro.chDedicada : "<i>Nao informada</i>" }
					</td>
					<td>${membro.funcaoMembro.descricao}</td>
					<td>
						<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=removeMembro&posLista=${status.index}" onclick="${confirmDelete}">
							<img src="${ctx}/img/delete.gif" alt="Remover Membro" title="Remover Membro" />
						</html:link>
					</td>
		      </tr>
		  </c:forEach>
		</c:when>
		<c:otherwise>
			<tr>
				<td align="center" style="font-style: italic; padding: 10px;" colspan="5">
					Ainda não foram informados os membros deste projeto
				</td>
			</tr>
		</c:otherwise>
	</c:choose>
	<tfoot class="formulario">
		<tr>
			<td colspan="6">
				<c:choose>
					<c:when test="${ not projetoPesquisaForm.renovacao }">
						<html:button dispatch="gravar" value="Gravar e Continuar"/>
						<c:choose>
							<c:when test="${ projetoPesquisaForm.financiado }">
								<html:button view="financiamentos" value="<< Voltar"/>
							</c:when>
							<c:otherwise>
								<html:button view="descricao" value="<< Voltar"/>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<html:button view="dadosRenovacao" value="<< Voltar"/>
					</c:otherwise>
				</c:choose>

				<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
				<html:button dispatch="cronograma" value="Avançar >>"/>
			</td>
		</tr>
	</tfoot>
</table>

<br/>
<div class="obrigatorio">Campos de preenchimento obrigatório</div>

</html:form>

<script>
var Abas = function() {

	var setCategoria = function(e, aba) {
		var idAba = aba.id;
		var categoria = getEl('categoriaMembro');
		habilitarParticipacoes(null, null, null);  
		switch(idAba) {
			case 'membro-docente': categoria.dom.value = <%= CategoriaMembro.DOCENTE %>; break;
			case 'membro-discente': 
				categoria.dom.value = <%= CategoriaMembro.DISCENTE %>; 
				habilitarParticipacoes(true, true, null); 
				$('radioColaborador').checked = true; 
				break;
			case 'membro-servidor': 
				categoria.dom.value = <%= CategoriaMembro.SERVIDOR %>;
				habilitarParticipacoes(true, true, null);
				$('radioColaborador').checked = true; 
				break;
			case 'membro-externo': 
				categoria.dom.value = <%= CategoriaMembro.EXTERNO %>;
				habilitarParticipacoes(null, null, null);
				$('radioColaborador').checked = true; 
				break;
		}
	};
	
	var habilitarParticipacoes = function(coordenador, vice, colaborador) {
		$('radioCoordenador').disabled = coordenador; 
		$('radioVice').disabled = vice; 
		$('radioColaborador').disabled = colaborador; 
	};
	
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('abas-membro');
			abas.on('tabchange', setCategoria);

	        abas.addTab('membro-docente', "Docente")
			abas.addTab('membro-discente', "Discente");
	        abas.addTab('membro-servidor', "Servidor Técnico-Administrativo");
			abas.addTab('membro-externo', "Externo");

			habilitarParticipacoes(null, null, null);  
			switch( getEl('categoriaMembro').dom.value ) {
				case ''+<%=CategoriaMembro.DOCENTE%>:  
					abas.activate('membro-docente'); 
					break;
				case ''+<%=CategoriaMembro.SERVIDOR%>:
					habilitarParticipacoes(true, true, null);
					$('radioColaborador').checked = true;
					abas.activate('membro-servidor'); 
					break;
				case ''+<%=CategoriaMembro.DISCENTE%>:  
					habilitarParticipacoes(true, true, null);
					$('radioColaborador').checked = true; 
					abas.activate('membro-discente'); 
					break;
				case ''+<%=CategoriaMembro.EXTERNO%>:
					habilitarParticipacoes(true, null, null);
					$('radioColaborador').checked = true;  
					abas.activate('membro-externo'); 
					break;
				default: abas.activate('membro-docente'); break;
			}
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);


var verificaEstrangeiro = function() {
	if ( $('checkEstrangeiro').checked ) {
		$('cpfExterno').disable();
	} else {
		$('cpfExterno').enable();
	}
}
verificaEstrangeiro();

window.onload = function() {
	buscarDocentePor('buscaAjaxDocenteUFRN');
	buscarDiscentePor('buscaAjaxMestrado');
};

function discenteOnFocus() {
	if( $('buscaAjaxResidente').checked )
		marcaCheckBox('buscaAjaxResidente');
	else if( !$('buscaAjaxGraduacao').checked && !$('buscaAjaxMestrado').checked && !$('buscaAjaxDoutorado').checked )
		marcaCheckBox('buscaAjaxMestrado');
}

</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
