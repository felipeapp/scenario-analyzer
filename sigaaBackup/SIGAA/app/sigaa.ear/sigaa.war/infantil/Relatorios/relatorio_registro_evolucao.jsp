<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<STYLE TYPE="text/css">
.quebraPagina {
page-break-after: always;
}
</STYLE>

<f:view>
	<h2>Relátorio do Registro de Evolução da Criança</h2>

	<c:set var="discente" value="#{registroEvolucaoCriancaMBean.obj.discente}"/>
	<c:set var="turma" value="#{registroEvolucaoCriancaMBean.obj.turma}"/>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td width="100%">
					<b>Turma:</b> ${turma.disciplina.nome } &nbsp;<b>Ano:</b> ${turma.ano } &nbsp;<b>Turno:</b> ${turma.descricaoHorario }
				</td>
			</tr>
			<tr>
				<td><b>Matrícula:</b> ${discente.matricula }</td>
			</tr>
			<tr>
				<td><b>Aluno:</b> ${discente.pessoa.nome } <b>Nascimento:</b> <ufrn:format type="data" valor="${discente.pessoa.dataNascimento}" /></td>
			</tr>
			<tr>
				<td><b>Mãe:</b> ${discente.pessoa.nomeMae } </td>
			</tr>
			<tr>
				<td><b>Pai:</b> ${discente.pessoa.nomePai } </td>
			</tr>
			<tr>
				<td><b>Professores:</b> ${turma.docentesNomes }</td>
			</tr>
			<tr>
				<td style="text-align: justify; ">
					<br/><br/><br/>
					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;O objetivo deste registro é possibilitar aos professores(as) e aos pais o
					acompanhamento da evolução da criança na escola: suas aprendizagens, dificuldades, e
					conquistas, tendo em vista os objetivos definidos para o grupo do qual ela faz parte e
					para ela mesma. Compreendemos que o desenvolvimento do indivíduo dá-se de forma
					global, não compartimentada. Entretanto, para objetivação do registro, as observações
					ou constatações sobre a criança serão agrupadas em duas grandes áreas dada á
					relação existentes entre elas, a saber; área de desenvolvimento sócio-afetivo e área de
					conhecimento (Linguagem Oral e Escrita, Mátemática, Arte, Educação Física e Ciências
					Naturais e Sociais).
					</p>
					<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Sendo o desenvolvimento um processo de interação, construção e reconstrução
					incessante e não linear, as observações descritas não definem a criança, mas a
					caracterizam em uma fase definida da sua vida, ou seja, expressam como ele mostrou-se
					em um determinado período, estando sujeita a constantes transformações. Devem,
					portanto, servir como um referencial à ação da escola e da família, no sentido de ajudar a
					criança a evoluir em seu desenvolvimento.</p>
					<br/><br/><br/><br/><br/>
					<b>OBS.: Esta ficha de registro pretende apresentar a relação existente entre os objetivos
					propostos e as competências apresentadas pela criança. Os aspectos que merecem mais
					atenção serão destacados nos itens de observações, dando a possibilidade para que os pais
					possam acompanhar melhor a criança, percebendo os avanços e as dificuldades.</b>
				</td>
			</tr>
		</table>
	</div>
	<br/>
	<div class="quebraPagina"></div>
	
	<table class="tabelaRelatorio" width="100%" border="1">
			<c:forEach items="#{registroEvolucaoCriancaMBean.formulario.areas}" var="area">
				<c:if test="${area.subareas != null and empty area.subareas}">
					<c:if test="${area.bloco != null}">
						<tr>
							<td style="text-align: inherit;"><b>${area.bloco.descricaoOrdem}</b></td>
						</tr>
					</c:if>
					<tr>
						<td style="text-align: inherit;"><b>${area.descricaoOrdem}</b></td>
					</tr>
					<tr>
						<td>
							<table class="tabelaRelatorio" width="100%" border="1">
								<thead>
									<tr>
										<th style="text-align: inherit;"> CONTEÚDOS/OBJETIVOS </th>
										<th width="6%" style="text-align: center; "> 1º BIM </th>
										<th width="7%" style="text-align: center; "> 2º BIM </th>
										<th width="6%" style="text-align: center; "> 3º BIM </th>
										<th width="7%" style="text-align: center; "> 4º BIM </th>
									</tr>
								</thead>
							</table>
						</td>
					</tr>
					<td>
						<table class="tabelaRelatorio" width="100%">
							<c:forEach items="#{area.conteudos}" var="conteudo">
								<tr>
									<td width="25%" style="text-align: inherit; border: 1px solid #888;" colspan="5"> ${conteudo.descricaoOrdem} </td>
									<td>
										<table width="100%">
											<c:forEach items="#{conteudo.objetivos}" var="objetivo">
												<tr class="relatorioBody">
													<td width="60%" style="text-align: left; border: 1px solid #888; padding: 3;"> ${objetivo.descricaoOrdem} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[0].descricaoResultado} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[1].descricaoResultado} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[2].descricaoResultado} </td>
													<td width="8%" style="text-align: center; border: 1px solid #888; padding: 3;"> ${objetivo.bimestres[3].descricaoResultado} </td>
												</tr>
											</c:forEach>
										</table>
									</td>
								</tr>
							</c:forEach>
						</table>
						<table border="1" width="100%">
							<tr>
								<td align="left" colspan="6"><b>Legenda:</b></td>
							</tr>
							<tr>
								<td align="center">${area.legendaFormaAvaliacao}</td>
							</tr>
						</table>
						<c:if test="${empty area.subareas}">
							</table>
							<br/>
							<table class="tabelaRelatorio" width="100%" border="1">
							<div class="quebraPagina"></div>
						</c:if>
					</td>
				</c:if>
			</c:forEach>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>