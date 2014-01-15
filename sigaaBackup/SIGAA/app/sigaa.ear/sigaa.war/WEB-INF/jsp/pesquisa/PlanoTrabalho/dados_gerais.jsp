<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa"%>

<c:if test="${empty naoCoordenador}">
<h2> <ufrn:subSistema /> > Dados Gerais </h2>

<script type="text/javascript">
	function docenteExterno(bool) {

		if(bool || bool == undefined){
  		  Element.show('docenteInterno');
		  Element.hide('docenteExterno');
			if($('orientadorExterno').value != 0 && bool != true){
				Element.show('docenteExterno');
				Element.hide('docenteInterno');
				$('orientador').value = 0;
				document.getElementById('externo').checked = true;
			}else{
				$('orientadorExterno').value = 0
				document.getElementById('interno').checked = true;
			}
		}else{
			Element.show('docenteExterno');
			Element.hide('docenteInterno');
		} 
	}
</script>

<html:form action="/pesquisa/planoTrabalho/wizard"  method="post" focus="obj.orientador.id">
<html:hidden property="obj.id" />

<c:if test="${ formPlanoTrabalho.obj.aguardandoResubmissao }">
	<div class="descricaoOperacao">
		<h3 style="text-align: center">
			Parecer do Avaliador
			(emitido em <ufrn:format type="data" name="formPlanoTrabalho" property="obj.dataAvaliacao" />
		</h3>
		<p style="margin: 10px 20px; text-indent: 20px;">
		 	${ formPlanoTrabalho.obj.parecerConsultor }
		</p>
	</div>
</c:if>
<c:if test="${ not formPlanoTrabalho.solicitacaoCota }">
	<div class="descricaoOperacao">
		<h3>
			Caro docente,
		</h3>
		<p style="margin: 10px 20px; text-indent: 20px;">
		 	Se o tipo de bolsa desejado não se encontrar disponível nas opções,
		 	entre em contato com a PROPESQ.
		</p>
		<p style="margin: 10px 20px; text-indent: 20px;">
		 	O formulário abaixo serve para a realização do cadastro dos dados gerais de um plano de trabalho.
		</p>
		
	</div>
</c:if>

	<table class="formulario" width="100%">
		<caption>Dados Gerais do Plano de Trabalho</caption>
	<tbody>
		<tr>
			<th width="14%"><b> Projeto de Pesquisa: </b> </th>
			<td> ${formPlanoTrabalho.obj.projetoPesquisa.codigoTitulo} </td>
		</tr>

		<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA %>">
				
		<tr>
			<td colspan="2" align="center">
				<input type="checkbox" id="alterarProjeto" name="alterarProjeto" class="noborder"/>
				<label for="alterarProjeto">Associar a outro Projeto de Pesquisa:</label>
				<html:text property="codigoProjeto.prefixo" size="3" maxlength="3" />
				<html:text property="codigoProjeto.numero" size="5" maxlength="7" onkeyup="formatarInteiro(this)"/>-<html:text property="codigoProjeto.ano" size="4" maxlength="4" onkeyup="formatarInteiro(this)"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" height="12"> </td>
		</tr>
		</ufrn:checkRole>
	
		<c:if test="${usuario.vinculoAtivo.vinculoDocenteExterno}">
			<tr>
				<td style="text-align: right;">Membro Externo: </td>
		 		<td>
					${usuario.pessoa.nome}			 		
		 		</td>
		 	</tr>
		</c:if>
		<tr>
			<td></td>
			<td colspan="2">
				
				<input name="radiobutton" type="radio" id="interno" 
					value="false" onClick="javascript:docenteExterno(true);">Orientador Interno

				<input name="radiobutton" type="radio" id="externo"
					value="false" onClick="javascript:docenteExterno(false);">Orientador Externo
			</td>
		</tr>
		<tr id="docenteInterno">
			<th class="obrigatorio">Orientador Interno:</th>
			<td height="30">
				<c:set var="membrosProjeto" value="${formPlanoTrabalho.referenceData.membrosProjeto}"/>
				<html:select property="obj.orientador.id" style="width: 90%" styleId="orientador">
					<html:option value="0">-- SELECIONE --</html:option>
					<html:options collection="membrosProjeto" property="servidor.id" labelProperty="pessoa.nome"/>
				</html:select>
			</td>
		</tr>

		<tr id="docenteExterno">
			<th class="obrigatorio">Orientador Externo: </th>
			<td height="30">
				<c:set var="membrosProjetoExterno" value="${formPlanoTrabalho.referenceData.membrosProjetoExterno}"/>
					<html:select property="obj.externo.id" style="width: 90%" styleId="orientadorExterno">
						<html:option value="0">-- SELECIONE --</html:option>
						<html:options collection="membrosProjetoExterno" property="docenteExterno.id" labelProperty="pessoa.nome"/>
					</html:select>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio"> Tipo ${formPlanoTrabalho.cadastroVoluntario? 'do Vínculo' : 'da Bolsa'}: </th>
			<td>
				<c:set var="tiposBolsa" value="${formPlanoTrabalho.referenceData.tiposBolsa}"/>
				<c:set var="tiposBolsaVoluntarios" value="${formPlanoTrabalho.referenceData.tiposBolsaVoluntarios}"/>
				<c:set var="tiposBolsaFluxoContinuo" value="${formPlanoTrabalho.referenceData.tiposBolsaFluxoContinuo}"/>

				<c:choose>
					<c:when test="${formPlanoTrabalho.permissaoGestor}">
						<select id="idTipoBolsa" name="idTipoBolsa" onchange="document.getElementById('dispatch').value = 'tratarTipoBolsa'; submit();">
							${ formPlanoTrabalho.outGroupStrutsGestor }
						</select>
					</c:when>
					<c:otherwise>			
						<c:if test="${formPlanoTrabalho.solicitacaoCota and not formPlanoTrabalho.cadastroVoluntario}">
							<input type="hidden" value=<%= "" + TipoBolsaPesquisa.A_DEFINIR %> name="idTipoBolsa" />
							A DEFINIR
						</c:if>
						<c:if test="${formPlanoTrabalho.solicitacaoCota and formPlanoTrabalho.cadastroVoluntario}">
							${ formPlanoTrabalho.outGroupStrutsVoluntario }
						</c:if>
						<c:if test="${not formPlanoTrabalho.solicitacaoCota}">
							 ${ formPlanoTrabalho.outGroupStrutsFluxoContinuo } 
						</c:if>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>

		<c:set var="tiposStatus" value="${formPlanoTrabalho.referenceData.tiposStatus}"/>
		<c:if test="${!empty tiposStatus}">
		<tr>
			<th> Status do Plano: </th>
			<td>
				${ formPlanoTrabalho.outGroupStatusPlano }
			</td>
		</tr>
		</c:if>
		<c:if test="${ formPlanoTrabalho.solicitacaoCota }">
			<tr>
				<th><b> Cota:</b></th>
				<td> ${formPlanoTrabalho.obj.cota} </td>
			</tr>
			<c:if test="${ not formPlanoTrabalho.cadastroVoluntario }">
				<tr>
					<th class="obrigatorio"> Edital: </th>
					<td>
					<c:choose>
						<c:when test="${formPlanoTrabalho.permissaoGestor}">
							<c:set var="editais" value="${formPlanoTrabalho.referenceData.editais}"/>
							<html:select property="obj.edital.id" style="width: 100%" onchange="document.getElementById('dispatch').value = 'tratarEditalPesquisa'; submit();">
								<html:option value="-1"> &nbsp; </html:option>
								<html:options collection="editais" property="id" labelProperty="descricao"/>
							</html:select>
						</c:when>
						<c:otherwise>
							${ formPlanoTrabalho.obj.edital.descricao }
						</c:otherwise>
					</c:choose>
					</td>
				</tr>
			</c:if>
		</c:if>
		<c:if test="${ formPlanoTrabalho.solicitacaoCota && !formPlanoTrabalho.cadastroVoluntario && not empty formPlanoTrabalho.referenceData.tipoBolsas}">
		<tr>
			<td colspan="2">
				<div class="descricaoOperacao">
					<p>
						Informe dentre os tipos de bolsa disponíveis no edital aquele que deseja concorrer. 
					</p>
				</div>	
			</td>	
		</tr>	
		<tr>
			<th class="obrigatorio">Tipo de Bolsa que deseja concorrer:</th>
			<td>
				<c:set var="bolsas" value="${formPlanoTrabalho.referenceData.tipoBolsas}" />
				<html:select property="obj.bolsaDesejada" style="width:15%">
					<html:option value="0">-- SELECIONE --</html:option>
			        <html:options collection="bolsas" property="id" labelProperty="descricaoResumida" />
		        </html:select>
			</td>
		</tr>
		</c:if>
		<c:if test="${ not formPlanoTrabalho.solicitacaoCota }">
			<tr>
				<th class="required">Período:</th>
				<td> <ufrn:calendar property="dataInicio" /> a <ufrn:calendar property="dataFim" /></td>
			</tr>
		</c:if>
		<tr>
			<th> <html:checkbox property="obj.continuacao" styleId="checkContinuacao"/> </th>
			<td> <label for="checkContinuacao">Este plano de trabalho é continuidade de algum plano do ano anterior?</label> </td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center">Corpo do Plano de Trabalho</td>
		</tr>
		<tr>
			<th class="required"> Título: </th>
			<td> <html:text property="obj.titulo" style="width:95%"/></td>
		</tr>

			<tr>
				<td colspan="2">
				<div id="abas-descricao">
		
					<div id="introducao-justificativa" class="aba">
						<i>Introdução e Justificativa do Plano de trabalho.</i><span class="required"></span><br /><br />
						<html:textarea property="obj.introducaoJustificativa" rows="8" style="width:95%" />
					</div>
					<div id="objetivos" class="aba">
						<i>Objetivos.</i><span class="required"></span><br /><br />
						<html:textarea property="obj.objetivos" rows="8" style="width:95%" />
					</div>
					<div id="metodologia" class="aba">
						<i>Metodologia.</i><span class="required"></span><br /><br />			
						<html:textarea property="obj.metodologia" rows="8" style="width:95%" />
					</div>
					<div id="referencia" class="aba">
						<i>Referência.</i><span class="required"></span><br /><br />			
						<html:textarea property="obj.referencias" rows="8" style="width:95%" />
					</div>
				</div>
				</td>
			</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="cancelar" cancelar="true">Cancelar</html:button>
				<c:if test="${ formPlanoTrabalho.obj.id > 0 }">
					<ufrn:checkRole papel="<%= SigaaPapeis.GESTOR_PESQUISA %>">
						<html:button dispatch="gravarDadosGerais">Gravar</html:button>
					</ufrn:checkRole>
				</c:if>
				<html:button dispatch="submeterDadosGerais">Definir Cronograma &gt;&gt;</html:button>
			</td>
		</tr>
	</tfoot>
	</table>
	<br/>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</html:form>
</c:if>

<script>
	docenteExterno();
</script>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('introducao-justificativa', "Introdução e Justificativa");
        abas.addTab('objetivos', "Objetivos");
		abas.addTab('metodologia', "Metodologia");
		abas.addTab('referencia', "Referências");
        abas.activate('introducao-justificativa');
    }
};


YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
YAHOO.ext.EventManager.onDocumentReady(Abas2.init, Abas2, true);
</script>

<c:if test="${ formPlanoTrabalho.alterarProjeto }">
	<script> $('alterarProjeto').checked = true; </script>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>