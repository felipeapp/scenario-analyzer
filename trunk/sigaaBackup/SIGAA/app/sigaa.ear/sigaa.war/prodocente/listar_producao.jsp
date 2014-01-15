<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2 class="title"><ufrn:subSistema /> > Listar as Produções Intelectuais</h2>
<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
<h:form>
<table class="listagem">
	<tr>
		<td class="subFormulario" colspan="2">
		Publicações
		</td>
	</tr>
	<tr>
		<td colspan="2">

			<table style="width:100%">
				<tr>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Artigo.gif" action="#{artigo.listar}"/><br />
						Artigo, Periódicos, Jornais e Similares </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Capitulos.gif" action="#{capitulo.listar}"/><br />
						Capítulo de Livros </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Livro.gif" action="#{livro.listar}" /><br />
						Livros </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/publicacoesEmEvento.gif" action="#{publicacaoEvento.listar}" /><br />
						Participação em Eventos </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/TextoDidaticoDiscussao.gif" action="#{textoDidatico.listar}" /><br />
						Textos Didáticos e Discussão </td>
				</tr>
			</table>


		</td>
	</tr>

	<tr>
		<td class="subFormulario" colspan="2">
		Artísticas, Literária e Visual
		</td>
	</tr>
	<tr>
		<td colspan="2">

			<table width="100%">
				<tr>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/AudioVisuais.gif" action="#{audioVisual.listar}" /><br />
					AudioVisuais </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/ExposicaoApresentacao.gif" action="#{exposicao.listar}" /><br />
					Exposição ou Apresentação Artísticas </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Montagem.gif" action="#{montagem.listar}" /><br />
					Montagens </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/ProgramacaoVisual.gif" action="#{programacao.listar}" /><br />
					Programação Visual </td>
				</tr>
			</table>


		</td>
	</tr>

	<tr>
		<td class="subFormulario">
		Tecnológicas
		</td>
		<td width="50%" class="subFormulario" style="border-left: 20px solid #FFF;">
		Bancas
		</td>
	</tr>
	<tr>
		<td>

			<table width="100%">
				<tr>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Maquete.gif" action="#{maquetePrototipoOutro.listar}" /><br />
					Maquetes, Protótipos, Softwares e Outros </td>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Patente.gif" action="#{patente.listar}" /><br />
					Patentes </td>
				</tr>
			</table>
		</td>

		<td>

			<table width="100%">
				<tr>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/banca_curso.gif" action="#{banca.listaCurso}" /><br />
					Trabalhos de conclusão </td>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/banca_concurso.gif" action="#{banca.listaConcurso}" /><br />
					Comissões Julgadoras </td>
				</tr>
			</table>
		</td>

	</tr>

	<tr>
		<td class="subFormulario" colspan="2">
		Outras
		</td>
	</tr>

	<tr>
		<td colspan="2">

			<table>
				<tr>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/premioRecebido.gif" action="#{premioRecebido.listar}" /><br />
						Prêmio Recebido</td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/BolsaObtida.gif" action="#{bolsaObtida.listar}" /><br />
						Bolsas Obtidas </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/VisitaCientifica.gif" action="#{visitaCientifica.listar}" /><br />
						Visitas Científicas </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/organizacaoEventos.gif" action="#{participacaoComissaoOrgEventos.listar}" /><br />
						Organização de Eventos, Consultorias, Edição e Revisão de Períodicos </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/participacaoSociedadeCientificaCutural.gif" action="#{participacaoSociedade.listar}" /><br />
						Participação em Sociedades Científicas e Culturais </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/colegiadosComissoes.gif" action="#{participacaoColegiadoComissao.listar}" /><br />
						Participação em Colegiados e Comissões </td>
				</tr>
			</table>
		</td>
	</tr>

</table>

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>