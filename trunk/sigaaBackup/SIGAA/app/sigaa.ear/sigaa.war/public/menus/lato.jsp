<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<dl>
	<dt>
		<div class="opcao curso-latos">
		<h3>Cursos de Lato-Sensu</h3>
		
		<a href="/sigaa/public/curso/lista.jsf?nivel=L&aba=p-lato">
		Consulte os cursos de especialização oferecidos pela ${ configSistema['siglaInstituicao'] }.
		</a>
		</div>
	</dt>
	<dt>
		<div class="opcao selecao">
		<h3>Processos Seletivos  </h3>
		<a href="/sigaa/public/processo_seletivo/lista.jsf?nivel=L&aba=p-lato">
			Consulte os processos seletivos abertos, inscreva-se ou gerencie suas inscrições.
		</a>
		</div>
	</dt>
	<dt>
		<div class="opcao componentes">
		<h3>Componentes Curriculares</h3>
		<a href="/sigaa/public/componentes/busca_componentes.jsf?nivel=L&aba=p-lato">
			Consulte os detalhes e os programas dos componentes curriculares ministrados nos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
		</a>
		</div>
	</dt>
</dl>