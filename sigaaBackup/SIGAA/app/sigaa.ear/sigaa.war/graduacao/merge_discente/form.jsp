<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
JAWR.loader.style('/bundles/css/sigaa_base.css', 'all');
JAWR.loader.style('/css/ufrn_print.css', 'print');
JAWR.loader.script('/javascript/jquery/jquery.js');

J = jQuery.noConflict();
function habilitarDetalhes(idDiscente) {
	var linha = 'linha_'+ idDiscente;
	if ( J('#'+linha).css('display') == 'none' ) {
		if (/msie/.test( navigator.userAgent.toLowerCase() ))
			J('#'+linha).css('display', 'inline-block');
		else
			J('#'+linha).css('display', 'table-cell');			
		
		if (J('#'+linha).html() != null) {
			if (document.getElementById('form:indicator_'+idDiscente) != null)
			   document.getElementById('form:indicator_'+idDiscente).style.display = 'block';
			
			new Ajax.Request("/sigaa/graduacao/merge_discente/dados_discente.jsf?idDiscente=" + idDiscente, {
				onComplete: function(transport) {
					J('#'+linha).html(transport.responseText);
					J('#'+linha).addClass('populado');
					if (document.getElementById('form:indicator_'+idDiscente) != null)
						document.getElementById('form:indicator_'+idDiscente).style.display = 'none';
				}
			});			
		}
	} else {
		J('#'+linha).css('display', 'none');		
	}
}

function marcarSomenteEste(chk) {
	var valor = chk.checked;
	var re= new RegExp('discente_', 'g')
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].id.match(re)) {
			elements[i].checked = false;
		}
	}
	chk.checked = valor;
}
</script>

<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	table.listagem td.detalhesDiscente { display: none; padding: 0;}
	.dadosDiscenteRadio {width: 100%; }
	table.dadosDiscenteRadio tbody tr td {
		width: 50% !important;
	}
</style>


<f:view>
	<h2><ufrn:subSistema /> > Unificar Dados Pessoais do Discente</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Usuário</b></p>
		<p>Este formulário permite unificar os dados pessoais de dois cadastros de discentes e deve ser realizado com muita atenção.</p>
		<p>Escolha qual dos discentes tem os dados pessoais mais atualizados. Ambos discentes ficarão somente com os dados pessoais em comum escolhido pelo usuário, as demais informações relativas ao discente ficam inalteradas.</p> 
	</div>
	<br />
		<center>
			<div class="infoAltRem" style="width:90%;"> 
				<h:graphicImage value="/img/comprovante.png" style="overflow: visible;" />: Visualizar Detalhes
			</div>
		</center>
		
		<h:form id="form">
		<table class="listagem" style="width:100%;">
			<caption> Discentes a Unificar</caption>
			<thead>
				<tr>
					<th width="2%"> </th>
					<th width="10%" style="text-align: center;"> Matrícula </th>
					<th width="30%"> Nome </th>
					<th> Curso </th>
					<th width="12%"> Status </th>
					<th width="2%"> </th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{mergeDadosDiscenteMBean.discentes}" var="discente" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td >
						<h:selectBooleanCheckbox value="#{discente.selecionado}" id="discente_${ discente.id }"
							onchange="marcarSomenteEste(this)"/>
					</td>				
					<td width="9%" style="text-align: center;">${discente.matricula}</td>
					<td>${discente.nome}</td>
					<td>${ discente.curso.descricaoCompleta } (${ discente.nivelDesc })
					</td>
					<td width="8%">${discente.statusString}</td>
					<td align="right" width="2%" ${ (discente.graduacao || discente.stricto) ? 'width="35" nowrap="nowrap"' : '' }>
						<a href="javascript: void(0);" onclick="habilitarDetalhes(${discente.id});" title="Visualizar Detalhes">
							<img src="${ctx}/img/comprovante.png" />
							<h:graphicImage value="/img/indicator.gif" id="indicator_${discente.id}" style="display: none;" />
						</a>							
					</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
					<td colspan="6" id="linha_${discente.id}" class="detalhesDiscente"></td>				
				</tr>
			</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
					<h:commandButton value="Adicionar Outro Discente" action="#{mergeDadosDiscenteMBean.formBuscaDiscente}" id="buscar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{mergeDadosDiscenteMBean.cancelar}" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{mergeDadosDiscenteMBean.confirmar}" id="confirmar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
